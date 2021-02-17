import pandas as pd
import sys, json

lines = sys.stdin.readlines()
filepath = lines[0].replace('\n', '')
data = json.loads(lines[1])
columns_to_select = []

df = pd.read_csv(filepath)

for column in data:
    if data[column][0] == True:
        columns_to_select.append(column)
        if data[column][2] == "string":
            if (data[column][1] == "mean"):
                df[column].fillna(df[column].mean(), inplace=True)
            else:
                df[column].fillna(df[column].median(), inplace=True)
        else:
            df[column].fillna('None', inplace=True)

df = df[columns_to_select]


df.to_csv(filepath.split('.csv')[0]+'_preprocessed.csv', mode='w', index=False)
print(filepath.split('.csv')[0]+'_preprocessed.csv')
