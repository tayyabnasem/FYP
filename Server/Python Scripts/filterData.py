from sklearn import preprocessing
import pandas as pd
import sys, json

lines = sys.stdin.readlines()
filepath = lines[0].replace('\n', '')
data = json.loads(lines[1])
columns_to_select = []

df = pd.read_csv(filepath)

over_all_data_options = data['over_all_dataset_options']
data = data['column_wise_options']

#print(column_wise_data_options)

for column in data:
    if data[column]['include'] == True:
        columns_to_select.append(column)

df = df[columns_to_select]

if (over_all_data_options['drop_rows']):
    df.dropna(inplace=True)
else:
    for column in columns_to_select:
        if data[column]['type'] != "String":
            if (data[column]['impute_int_with'] == "mean"):
                df[column].fillna(df[column].mean(), inplace=True)
            else:
                df[column].fillna(df[column].median(), inplace=True)
        else:
            df[column].fillna(data[column]['impute_str_with'], inplace=True)

for column in data:
    if data[column]['type'] == "String":
        df[column] = preprocessing.LabelEncoder().fit_transform(df[column])

df.to_csv(filepath.split('.csv')[0]+'_preprocessed.csv', mode='w', index=False)
print(filepath.split('.csv')[0]+'_preprocessed.csv')
