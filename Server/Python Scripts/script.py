import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import sys,json, random

lines = sys.stdin.readlines()
filepath = lines[0].replace('\n', '')
data = json.loads(lines[1])

df = pd.read_csv(filepath)

if data['plottype'] == 'scatter':
    sns.scatterplot(x=df[data['x']], y=df[data['y']], hue=df[data['y']])
    #ax = df.plot(x = data['x'], y = data['y'], kind = 'scatter')
elif data['plottype'] == 'bar':
    if data['x'] == data['y']:
        sns.histplot(x=df[data['x']], hue=df[data['y']])
    else:
        sns.histplot(x=df[data['x']], hue=df[data['y']],element='step')
    # ax = df.plot(x = data['x'], y = data['y'], kind = 'bar')
else:
    sns.lineplot(x=df[data['x']], y=df[data['y']], hue=df[data['y']])
    #ax = df.plot(x = data['x'], y = data['y'], kind = 'line')
filename = "./plotscatter"+str(random.randint(0,100))+".png"
print(filename)
plt.savefig(filename)