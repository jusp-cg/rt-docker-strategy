from flask import Flask
app = Flask(__name__)

@app.route('/')
def hello():
    return  """
<html>
	<head><h2>Example Application<h2></head>
	<body>
		<h4>Welcome</h4>
	</body>
</html>
    """
    
@app.route('/healthcheck')
def health():
    return  """
<html>
	<head><h2>Example Application<h2></head>
	<body>
		<h4>Server is up</h4>
	</body>
</html>
    """

if(__name__ == '__main__'):
    app.run(host='0.0.0.0')    