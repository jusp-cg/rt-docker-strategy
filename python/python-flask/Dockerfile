FROM python:3.6-slim

WORKDIR /app

ADD . /app

RUN pip install --trusted-host cgrepo.capgroup.com --index https://cgrepo.capgroup.com/repository/cgpypi/pypi/ --index-url https://cgrepo.capgroup.com/repository/cgpypi/simple/ -r requirements.txt

EXPOSE 5000

CMD ["python", "app.py"]