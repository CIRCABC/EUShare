import os
import json

project_directory = 'src'
translation_directory = 'src/assets/i18n'
exclude_directory = translation_directory

def read_json_file(filepath):
    with open(filepath, 'r') as f:
        return json.load(f)

def write_json_file(filepath, data):
    with open(filepath, 'w') as f:
        json.dump(data, f, indent=4)

translation_files = [os.path.join(translation_directory, f) for f in os.listdir(translation_directory) if f.endswith('.json')]
reference_translations_path = os.path.join(translation_directory, 'en.json')
reference_translations = read_json_file(reference_translations_path)
keys_to_remove = []

for key in reference_translations:
    key_found = False
    for root, dirs, files in os.walk(project_directory):
        if exclude_directory in root:
            continue
        for file_name in files:
            file_path = os.path.join(root, file_name)
            try:
                with open(file_path, 'r', encoding='utf-8', errors='ignore') as file:
                    if key in file.read():
                        key_found = True
                        break
            except Exception as e:
                print(f"Error reading {file_path}: {e}")
        if key_found:
            break
    if not key_found:
        keys_to_remove.append(key)

for translation_file in translation_files:
    translations = read_json_file(translation_file)
    for key in keys_to_remove:
        if key in translations:
            del translations[key]
    write_json_file(translation_file, translations)

print(f"Removed {len(keys_to_remove)} unused translation keys from {len(translation_files)} files.")
