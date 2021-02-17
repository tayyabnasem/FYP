import pandas as pd
import sys,json, random

lines = sys.stdin.readlines()
filepath = lines[0].replace('\n', '')
data = json.loads(lines[1])

df = pd.read_csv(filepath)

ax = df.plot(x = data['x'], y = data['y'], kind = 'scatter')
filename = "./plotscatter"+str(random.randint(0,100))+".png"
print(filename)
ax.figure.savefig(filename)


