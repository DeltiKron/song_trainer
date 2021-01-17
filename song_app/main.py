import os
import requests
from flask import Flask, render_template, request
from flask_bootstrap import Bootstrap
from flask_sqlalchemy import SQLAlchemy
from requests.auth import HTTPBasicAuth

from songs_db.data_classes import Song
from songs_db.db import get_db


app = create_app()
