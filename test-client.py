import requests

fileName = "hello-world"
replicateNumber = 2
api_url = f"http://localhost:2206/file/{fileName}/content/{replicateNumber}"
content = "This is hello world!"

response = requests.post(api_url, data=content)

print(response)
