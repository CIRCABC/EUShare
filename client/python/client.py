import requests
import json
import os

baseurl = "https://circabc.acceptance.europa.eu/share"
API_KEY = "YOUR-API-KEY"
file_to_upload = "./text.txt"

##### login #####
url = baseurl+"/webservice/login"
headers = {
    "Content-Type": "application/json",
    "X-API-KEY": API_KEY
}
response = requests.post(url, headers=headers)

user_id = response.json()["userId"]
print(f"Login successful. User ID: {user_id}")


##### list shared files #####
url = baseurl+"/webservice/user/{}/files/fileInfoUploader".format(user_id)
params = {
    "pageSize": 10, "pageNumber": 0
}

response = requests.get(url, headers=headers,params=params)

files = response.json()
print(f"List shared files successful. Files {files}")


###### request file space #####
url = baseurl+"/webservice/file/fileRequest"
payload = json.dumps({
  "expirationDate": "2023-06-30",
  "hasPassword": False,
  "name": os.path.basename(file_to_upload),
  "size": 1024,
  "sharedWith":[{"email":""}],"downloadNotification":False
})


response = requests.post(url, headers=headers,data=payload)   

file_id = response.json()["fileId"]
print(f"File space request successful. File ID: {file_id}")


##### upload file #####
url = baseurl+"/webservice/file/{}/fileRequest/fileContent".format(file_id)
file_path = file_to_upload
headers.pop("Content-Type") 

files = {"file": ("text.txt", open(file_path, "rb"), "application/octet-stream")}

response = requests.post(url, headers=headers,files=files)

print(f"File upload successful. File ID: {file_id}")


##### list shared files #####
url = baseurl+"/webservice/user/{}/files/fileInfoUploader".format(user_id)
params = {
    "pageSize": 10, "pageNumber": 0
}

response = requests.get(url, headers=headers,params=params)

files = response.json()
print(f"List shared files successful. Files {files}")





