import requests

fileName = "hello-world"
api_url = f"http://localhost:8080/file/{fileName}/content"
content = "This is hello world!"

response = requests.post(api_url, data=content)

print(response)
