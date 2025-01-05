def second():
    import re

    def json_to_yaml(json_str):

        pattern_mas_1 = r'\}\,\{"(\w+)":'             # преобразует },{"key": в - key:
        pattern_mas_2 = r'\[\{"(\w+)":'               # преобразует [{"key": в - key:
        pattern_key = r'"(\w+)":'                     # преобразует "key": в key:
        pattern_empty_lines = r'\n\s*\n'              # убираем лишние пустые строки
        pattern_nested_objects = r'(\n\s*)(\w+):'     # обрабатываем вложенные объекты
        pattern_list_elements = r'(\n\s*)(- )'        # преобразует элементы списка с отступами
        pattern_double_quotes = r'"([^"]+)"'          # убирает лишние кавычки

        yaml_str = re.sub(pattern_mas_1, r'- \1:', json_str)
        yaml_str = re.sub(pattern_mas_2, r'- \1:', yaml_str)
        yaml_str = re.sub(pattern_key, r'\1:', yaml_str)

        yaml_str = re.sub(r'\{', '', yaml_str)
        yaml_str = re.sub(r'\}', '', yaml_str)
        yaml_str = re.sub(r'\[', '', yaml_str)
        yaml_str = re.sub(r'\]', '', yaml_str)
        yaml_str = re.sub(r',', '', yaml_str)

        yaml_str = re.sub(pattern_nested_objects, r'\1  \2:', yaml_str)

        yaml_str = re.sub(pattern_empty_lines, '\n', yaml_str)

        yaml_str = re.sub(pattern_list_elements, r'\1  \2', yaml_str)

        yaml_str = re.sub(pattern_double_quotes, r'\1', yaml_str)

        return yaml_str.strip()

    with open('schedule.json', 'r', encoding='utf-8') as json_file:
        json_data = json_file.read()

    yaml_result = json_to_yaml(json_data)
    with open('schedule_dop2.yaml', 'w', encoding='utf-8') as yaml_file:
        yaml_file.write(yaml_result)

second()