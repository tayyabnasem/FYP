import pandas as pd
import sys, json

lines = sys.stdin.readlines()
filepath = lines[0]
#filepath = 'D:\\FYP\\Code\\Server\\Uploads\\NCHS.csv'

response = []

df = pd.read_csv(filepath)
columns = df.columns.values

for i in range(len(columns)):
    if df[columns[i]].dtypes.name == 'object':
        response.append({
            'name': columns[i],
            'type': 'String',
            'unique': str(df[columns[i]].nunique()),
            'missing': str(df[columns[i]].isnull().sum()),
            'mean': '',
            'min': '',
            'max': '',
            'std': '',
            'labels': df[columns[i]].unique().tolist()
        })
    else:
        response.append({
            'name': columns[i],
            'type': 'Numerical',
            'unique': str(df[columns[i]].nunique()),
            'missing': str(df[columns[i]].isnull().sum()),
            'mean': str(df[columns[i]].mean()),
            'min': str(df[columns[i]].min()),
            'max': str(df[columns[i]].max()),
            'std': str(df[columns[i]].std()),
            'labels': []
        })

json_obj = json.dumps(response)
print(json_obj)