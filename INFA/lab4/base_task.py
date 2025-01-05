"""
Вариант 26
JSON ---> YAML
день недели: Среда, пятница
"""

def base_task():

    import json

    def json_to_yaml(json_obj, indent=0, flag=1):
        yaml_str = ""
        space = "  "

        if isinstance(json_obj, dict):
            for key, value in json_obj.items():
                if flag:
                    yaml_str += f"{space * indent}{key}:"
                else:
                    yaml_str += f"{key}:"
                    indent += 1
                if isinstance(value, (dict, list)):
                    yaml_str += "\n" + json_to_yaml(value, indent + 1)
                else:
                    yaml_str += f" {value}\n"
        elif isinstance(json_obj, list):
            for item in json_obj:
                yaml_str += f"{space * indent}- "
                if isinstance(item, (dict, list)):
                    yaml_str += json_to_yaml(item, indent, 0)
                else:
                    yaml_str += f"{item}\n"

        return yaml_str

    with open('schedule.json', 'r', encoding='utf-8') as json_file:
        schedule = json.load(json_file)

    with open('schedule.yaml', 'w', encoding='utf-8') as yaml_file:
        yaml_file.write(json_to_yaml(schedule))

base_task()
