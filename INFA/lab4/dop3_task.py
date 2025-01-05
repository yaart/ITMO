def third():
    def parse_json(json_str):
        json_str = json_str.strip()

        def parse_value():
            nonlocal json_str
            json_str = json_str.strip()

            if json_str.startswith('"'):
                return parse_string()
            elif json_str.startswith('{'):
                return parse_object()
            elif json_str.startswith('['):
                return parse_array()
            else:
                raise ValueError(f"Invalid JSON syntax: Unexpected character '{json_str[0]}'")

        def parse_string():
            nonlocal json_str
            json_str = json_str.strip()
            if json_str[0] != '"':
                raise ValueError(f"Invalid JSON string: Expected '\"' but found '{json_str[0]}'")

            json_str = json_str[1:]  # Убираем начальную кавычку
            result = ''
            while json_str and json_str[0] != '"':
                result += json_str[0]
                json_str = json_str[1:]

            if not json_str:
                raise ValueError("Invalid JSON string: Missing closing '\"'")

            json_str = json_str[1:]  # Убираем закрывающую кавычку
            return result

        def parse_object():
            nonlocal json_str
            json_str = json_str.strip()
            if json_str[0] != '{':
                raise ValueError(f"Invalid JSON object: Expected '{{' but found '{json_str[0]}'")

            json_str = json_str[1:]  # Убираем открывающую скобку
            obj = {}
            while json_str and json_str[0] != '}':
                key = parse_string()
                json_str = json_str.strip()
                if json_str[0] != ':':
                    raise ValueError("Expected ':' after key in object")
                json_str = json_str[1:]  # Убираем двоеточие
                value = parse_value()
                obj[key] = value
                json_str = json_str.strip()
                if json_str[0] == ',':
                    json_str = json_str[1:]  # Пропускаем запятую
                elif json_str[0] == '}':
                    break
                else:
                    raise ValueError("Expected ',' or '}'")

            if json_str[0] != '}':
                raise ValueError("Invalid JSON object: Missing closing '}'")

            json_str = json_str[1:]  # Убираем закрывающую скобку
            return obj

        def parse_array():
            nonlocal json_str
            json_str = json_str.strip()
            if json_str[0] != '[':
                raise ValueError(f"Invalid JSON array: Expected '[' but found '{json_str[0]}'")

            json_str = json_str[1:]  # Убираем открытую квадратную скобку
            arr = []
            while json_str and json_str[0] != ']':
                arr.append(parse_value())
                json_str = json_str.lstrip()
                if json_str[0] == ',':
                    json_str = json_str[1:]  # Пропускаем запятую
                elif json_str[0] == ']':
                    break
                else:
                    raise ValueError("Expected ',' or ']'")

            if json_str[0] != ']':
                raise ValueError("Invalid JSON array: Missing closing ']'")

            json_str = json_str[1:]  # Убираем закрывающую квадратную скобку
            return arr

        return parse_value()


    def to_yaml(obj, indent=0, flag=0):
        ind = ' ' * indent

        # Если объект - это словарь (объект JSON)
        if isinstance(obj, dict):
            result = []
            for key, value in obj.items():
                if flag:
                    result.append(f"{key}:\n{to_yaml(value, indent + 2)}")
                elif isinstance(value, dict) or isinstance(value, list):
                    result.append(f"{ind}{key}:\n{to_yaml(value, indent + 2)}")
                else:
                    result.append(f"{ind}{key}: {to_yaml(value, indent + 2)}")

            return '\n'.join(result)

        elif isinstance(obj, list):
            result = []
            for item in obj:
                result.append(f"{ind}- {to_yaml(item, indent + 2, 1)}")
            return '\n'.join(result)

        # Если это строка, нужно учитывать многострочные строки
        elif isinstance(obj, str):
            # Если строка слишком длинная, делаем её многострочной
            if '\n' in obj or len(obj) > 80:
                result = '\n'.join([f"{ind}|", *[f"{ind}  {line}" for line in obj.splitlines()]])
            else:
                result = f'"{obj}"' if '"' in obj else obj
            return result

        else:
            return str(obj)


    def json_to_yaml(json_str):
        parsed_json = parse_json(json_str)
        return to_yaml(parsed_json)


    with open('schedule.json', 'r', encoding='utf-8') as json_file:
        json_data = json_file.read()

    with open('schedule_dop3.yaml', 'w', encoding='utf-8') as yaml_file:
        yaml_file.write(json_to_yaml(json_data))

third()

