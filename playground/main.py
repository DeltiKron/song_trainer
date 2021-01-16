import os
import requests
from flask import Flask, render_template, request
from flask_bootstrap import Bootstrap
from flask_sqlalchemy import SQLAlchemy
from requests.auth import HTTPBasicAuth

from songs_db.data_classes import Song
from songs_db.db import get_db


def create_app(test_config=None):
    # create and configure the app
    app = Flask(__name__, instance_relative_config=True)
    Bootstrap(app)
    app.config.from_mapping(
        SECRET_KEY='dev',
        SQLALCHEMY_DATABASE_URI = 'sqlite:///'+os.path.join(app.instance_path, f'{__name__}.sqlite'),
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
    @app.route('/song_db')
    def songdb():
        song = dict(
            Title='Fuck her Gently!',
            Artist='Tenacious D',
            play_count=22,
            rating=4,
            song_id=20,
            notes='This is a Note'
        )
        return render_template('song.html', song=song)

    ## No Page, just a HTTP endpoint
    @app.route('/rate_song')
    def rate_song(sel):
        db = get_db()
        song_id = request.args.get('song_id')
        new_rating = request.args.get('rating')
        song = Song.query().filter_by(song_id=song_id)
        play_count = song.play_count()

        if play_count == 0:
            play_count = 1
            mean_rating = new_rating
        else:
            play_count+=1
            mean_rating = song.skill_level+((new_rating-new_rating)/play_count)

        db.session.query().filter_by(song_id=song_id).update(skill_level=mean_rating,play_count=play_count)

        print(f'Someone rated {song_id} with {new_rating} stars.')
        return f'Someone rated {song_id} with {new_rating} stars.'

    @app.route('/home')
    def start_page():
        return render_template('start_page.html')

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

        return render_template('observation_view.html', obs_id=obs_id, description=json_data, log_text=log_text,
                               pre_url=pre_url, loc_url=loc_url, obsname=obsname)

    from . import db
    db.init_app(app)

    return app


app = create_app()
