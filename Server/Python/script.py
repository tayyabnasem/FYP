import pandas as pd
import sys,json, random

lines = sys.stdin.readlines()

lines = json.loads(lines[0])

json_object = json.dumps(lines['data'])  
df = pd.read_json(json_object)

ax = df.plot(x = lines['x'], y = lines['y'], kind = 'scatter')
filename = "./plotscatter"+str(random.randint(0,100))+".png"
print(filename)
ax.figure.savefig(filename)
