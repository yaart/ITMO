import json
import csv


def json_to_csv(json_file, csv_file):
    with open(json_file, 'r', encoding='utf-8') as f:
        data = json.load(f)

    with open(csv_file, 'w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)

        headers = [
            "Day", "Lesson Number", "Title", "Type", "Begin Time", "End Time",
            "Teacher", "Building", "Classroom"]

        writer.writerow(headers)

        for day_info in data["schedule"]["day"]:
            for day, lessons in day_info.items():
                for les in lessons["lessons"]:
                    for lesson_number, lesson_info in les.items():

                        lesson = lesson_info
                        row = [
                            day,  # День
                            lesson_number,
                            lesson["title"],
                            lesson["type"],
                            lesson["begin_time"],
                            lesson["end_time"],
                            lesson["teacher"],
                            lesson["building"],
                            lesson["classroom"]
                        ]
                        writer.writerow(row)


json_to_csv('schedule.json', 'schedule.csv')
