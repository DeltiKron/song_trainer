from random import choices, shuffle

from flask import (
    Blueprint, render_template, request
)

from song_app.data_classes import Song, db

bp = Blueprint('songs', __name__, url_prefix='/songs')


@bp.route('/random')
def random_songs():
    songs = Song.query.all()
    shuffle(songs)
    return render_template('random_song_grid.html', songs=songs[:9])

@bp.route('/play_song/<song_id>')
def play_song(song_id):
    song = Song.query.get(song_id)
    print(song)
    return render_template('song.html',song=song)
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


@bp.route('/home')
def start_page():
    return render_template('start_page.html')
