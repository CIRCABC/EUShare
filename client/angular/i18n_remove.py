import os
import json

translation_directory = 'src/assets/i18n'

def read_json_file(filepath):
    with open(filepath, 'r') as f:
        return json.load(f)

def write_json_file(filepath, data):
    with open(filepath, 'w') as f:
        json.dump(data, f, indent=4)

reference_translations_path = os.path.join(translation_directory, 'en.json')
reference_translations = read_json_file(reference_translations_path)

translation_files = [os.path.join(translation_directory, f) for f in os.listdir(translation_directory) if f.endswith('.json') and f != 'en.json']

all_removed_keys = set()

for translation_file in translation_files:
    translations = read_json_file(translation_file)
    keys_to_remove = [key for key in translations if key not in reference_translations]
    
    for key in keys_to_remove:
        all_removed_keys.add(key)
        del translations[key]

    write_json_file(translation_file, translations)

print(f"Processed {len(translation_files)} files.")
print("Removed keys:")
for key in all_removed_keys:
    print(key)

