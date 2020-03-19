FROM python:3.6-slim

RUN date

WORKDIR /app

RUN date

ADD . /app

RUN date

RUN pip install --trusted-host cgrepo.capgroup.com --index https://cgrepo.capgroup.com/repository/cgpypi/pypi/ --index-url https://cgrepo.capgroup.com/repository/cgpypi/simple/ -r requirements.txt

RUN date

EXPOSE 5000

RUN date

CMD ["python", "app.py"]