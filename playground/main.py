import os
from pprint import pformat
import json
from urllib.request import urlopen

import requests
from flask import Flask, render_template
from flask_bootstrap import Bootstrap
from requests.auth import HTTPBasicAuth


def create_app(test_config=None):
    # create and configure the app
    app = Flask(__name__, instance_relative_config=True)
    Bootstrap(app)
    app.config.from_mapping(
        SECRET_KEY='dev',
        DATABASE=os.path.join(app.instance_path, f'{__name__}.sqlite'),
    )

    if test_config is None:
        # load the instance config, if it exists, when not testing
        app.config.from_pyfile('config.py', silent=True)
    else:
        # load the test config if passed in
        app.config.from_mapping(test_config)

    # ensure the instance folder exists
    try:
        os.makedirs(app.instance_path)
    except OSError:
        pass

    # a simple page that says hello
    @app.route('/hello')
    def hello():
        return render_template('hello.html')

    # a simple page that says hello
    @app.route('/song_db')
    def songdb():
        song=dict(
            Title='Foobar',
            Artist='Some Artist',
            Play_Count=22,
            rating=23,
            song_id=20,
            notes='This is a Note'
        )
        return render_template('song_db.html',song=song)


    # a simple page that links to base
    @app.route('/base')
    def base():
        return render_template('base.html')

    # json editor test page
    @app.route('/jsoneditor')
    def jsonedit():
        return render_template('json_editor_test.html')

    # a page that displays an observation
    @app.route('/observation/<obs_id>')
    def observation(obs_id):
        json_url = f"http://localhost:8080/gris_observations/{obs_id}"
        response = requests.get(json_url, auth=HTTPBasicAuth('admin', 'secret'))
        json_data = response.json()

        log_id = json_data['links']['obs_log'][0]['$oid']
        log_url = (f"http://localhost:8080/gris_obs_log.files/{str(log_id)}/binary")
        response = requests.get(log_url, auth=HTTPBasicAuth('admin', 'secret'))
        log_text = response.content.decode('utf8').strip()

        loc_id = json_data['links']['obs_location'][0]['$oid']
        loc_url = (f"http://localhost:8080/gris_obs_location.files/{str(loc_id)}/binary")

        pre_id = json_data['links']['obs_preview'][0]['$oid']
        pre_url = (f"http://localhost:8080/gris_obs_preview.files/{str(pre_id)}/binary")

        obsname = json_data['description']['OBS_NAME']

        return render_template('observation_view.html',obs_id=obs_id,description=json_data, log_text = log_text, pre_url=pre_url, loc_url=loc_url, obsname=obsname)

    from . import db
    db.init_app(app)

    return app

app = create_app()