from youtube_dl import YoutubeDL

options = {
    "format": "bestaudio/best",
    "postprocessors": [{
        'key': 'FFmpegExtractAudio',
        'preferredcodec': 'mp3',
        'preferredquality': '192',
    }]
}

with open("liens.txt") as f:
    url_list = f.readlines()

with YoutubeDL(options) as ydl:
    ydl.download(url_list)
