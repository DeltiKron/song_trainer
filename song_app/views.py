from random import shuffle

from flask import (
    Blueprint, render_template, request, url_for, flash
)
from werkzeug.utils import redirect

from song_app.data_classes import Song, db

bp = Blueprint('songs', __name__, url_prefix='/songs')


@bp.route('/random')
def random_songs():
    songs = Song.query.all()
    shuffle(songs)
    return render_template('random_song_grid.html', songs=songs[:9])


@bp.route('/random_song')
def get_random_song():
    songs = Song.query.all()
    shuffle(songs)
    return render_template('song.html', song=songs[0])


@bp.route('/play_song/<song_id>')
def play_song(song_id):
    song = Song.query.get(song_id)
    print(song)
    return render_template('song.html', song=song)


# ## No Page, just a HTTP endpoint
@bp.route('/rate_song')
def rate_song():
    song_id = request.args.get('song_id')
    new_rating = float(request.args.get('rating'))
    song = Song.query.filter_by(id=song_id).first()
    play_count = song.play_count

    if play_count == 0:
        play_count = 1
        mean_rating = new_rating
    else:
        play_count += 1
        mean_rating = song.skill_level + ((new_rating - song.skill_level) / play_count)

    song.skill_level = mean_rating
    song.play_count = play_count
    db.session.commit()

    print(f'Someone rated {song_id} with {new_rating} stars.')
    return f'Someone rated {song_id} with {new_rating} stars.'


@bp.route("list_songs")
def list_songs():
    songs = Song.query.all()
    return render_template('list_songs.html', songs=songs)


@bp.route('/add_song', methods=('GET', 'POST'))
def add_song():
    if request.method == 'POST':
        title = request.form['title']
        artist = request.form['artist']
        notes = request.form['notes']
        error = None

        if not title:
            error = 'Title is required.'
        elif not artist:
            error = 'Artist is required.'

        if error is None:
            duplicates = db.session.query(Song).filter(Song.title == title, Song.artist == artist).all()
            if len(duplicates) >= 1:
                error = "Song already in DB!"
        if error is None:
            song = Song(title=title, artist=artist, skill_level=0, play_count=0, notes=notes)
            db.session.add(song)
            db.session.commit()
            return redirect(url_for('songs.play_song', song_id=song.id))
        flash(error)

    return render_template('add_song.html')


@bp.route('/home')
def start_page():
    return render_template('start_page.html')
