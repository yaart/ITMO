def first():

    import json
    import yaml

    with open('schedule.json', 'r', encoding='utf-8') as json_file:
        json_data = json.load(json_file)

    with open('schedule_dop1.yaml', 'w', encoding='utf-8') as yaml_file:
        yaml.dump(json_data, yaml_file, allow_unicode=True, default_flow_style=False)


first()