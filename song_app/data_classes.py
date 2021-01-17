from flask_sqlalchemy import SQLAlchemy

db = SQLAlchemy()
class Song(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    title = db.Column(db.String(80), nullable=False)
    artist = db.Column(db.String(80), nullable=True)
    play_count = db.Column(db.Integer)
    skill_level = db.Column(db.Float)
    notes = db.Column(db.String(80 * 20), nullable=True)

    def __repr__(self):
        return f"<song: {self.title} by {self.artist}>"

    def __str__(self):
        return f"Song: {self.title} by {self.artist}; played {self.play_count} times; skill_level {self.skill_level}"
