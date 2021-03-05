import pandas as pd
import sys, json
import numpy as np
from pandas.core.algorithms import value_counts

from pandas.core.base import DataError
from pandas.errors import EmptyDataError

lines = sys.stdin.readlines()
filepath = lines[0]
#filepath = 'D:\\FYP\\Code\\Server\\Uploads\\NCHS.csv'

response = []

try:
    df = pd.read_csv(filepath)
    #df = pd.read_csv("C:\\Users\\User\\Downloads\\IRIS.csv")
    #df = pd.read_csv("C:\\Users\\User\\Downloads\\archive (1)\weatherAUS.csv")
except  EmptyDataError:
    raise Exception("Dataset is empty")
else:
    columns = df.columns.values

    for i in range(len(columns)):
        if df[columns[i]].dtypes.name == 'object':
            labels = df[columns[i]].unique().tolist()
            labels = list(filter(lambda label: not label is np.nan, labels))
            label_dict = []
            value_counts = df[columns[i]].value_counts().values
            for j in range(len(labels)):
                label_dict.append([labels[j], str(value_counts[j])])

            response.append({
                'name': columns[i],
                'type': 'String',
                'unique': str(df[columns[i]].nunique()),
                'missing': str(df[columns[i]].isnull().sum()),
                'mean': '',
                'min': '',
                'max': '',
                'std': '',
                'labels': label_dict
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