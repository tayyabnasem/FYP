import os
import sys, json


def classification_algorithms(data, lines):
    lines.extend(
        [
            "from sklearn.metrics import classification_report",
            "from sklearn.metrics import confusion_matrix",
            "from sklearn.utils import shuffle"
        ]
    )
    if data["algorithm"] == "Random Forest":
        pass
    elif data["algorithm"] == "Decision Tree Classifier":
        pass
    elif data["algorithm"] == "Gaussian Naive Bayes":
        lines.extend(
            [
                "from sklearn.naive_bayes import GaussianNB",
                f"df = pd.read_csv(r'{filepath}')",
                "",
                f"if df['{data['parameters']['output_coulmn']}'].dtypes.name == 'object':",
                "\tdf = shuffle(df)",
                f"\tX = df.loc[:, df.columns != '{data['parameters']['output_coulmn']}'].copy()",
                f"\tY = df['{data['parameters']['output_coulmn']}'].copy()",
                "\tfor column in X.columns.values:",
                "\t\tif X[column].dtypes.name == 'object':",
                "\t\t\tX[column] = preprocessing.LabelEncoder().fit_transform(X[column])",
                "\tlabels = Y.unique().tolist()",
                f"\tX_train, X_test, y_train, y_test = train_test_split(X, Y, test_size={(100 - data['parameters']['validation_split']) / 100})",
                "\tgnb = GaussianNB()",
                "\ty_pred = gnb.fit(X_train, y_train).predict(X_test)",
            ]
        )
    elif data['algorithm'] == "Categorical Naive Bayes":
        lines.extend(
            [
                "from sklearn.naive_bayes import CategoricalNB",
                f"df = pd.read_csv(r'{filepath}')",
                "",
                f"if df['{data['parameters']['output_coulmn']}'].dtypes.name == 'object':",
                "\tdf = shuffle(df)",
                f"\tX = df.loc[:, df.columns != '{data['parameters']['output_coulmn']}'].copy()",
                f"\tY = df['{data['parameters']['output_coulmn']}'].copy()",
                "\tfor column in X.columns.values:",
                "\t\tif X[column].dtypes.name == 'object':",
                "\t\t\tX[column] = preprocessing.LabelEncoder().fit_transform(X[column])",
                "\tlabels = Y.unique().tolist()",
                f"\tX_train, X_test, y_train, y_test = train_test_split(X, Y, test_size={(100 - data['parameters']['validation_split']) / 100})",
                "\tgnb = CategoricalNB()",
                "\ty_pred = gnb.fit(X_train, y_train).predict(X_test)",
            ]
        )
    elif data['algorithm'] == "Multinomial Naive Bayes":
        lines.extend(
            [
                "from sklearn.naive_bayes import MultinomialNB",
                f"df = pd.read_csv(r'{filepath}')",
                "",
                f"if df['{data['parameters']['output_coulmn']}'].dtypes.name == 'object':",
                "\tdf = shuffle(df)",
                f"\tX = df.loc[:, df.columns != '{data['parameters']['output_coulmn']}'].copy()",
                f"\tY = df['{data['parameters']['output_coulmn']}'].copy()",
                "\tfor column in X.columns.values:",
                "\t\tif X[column].dtypes.name == 'object':",
                "\t\t\tX[column] = preprocessing.LabelEncoder().fit_transform(X[column])",
                "\tlabels = Y.unique().tolist()",
                f"\tX_train, X_test, y_train, y_test = train_test_split(X, Y, test_size={(100 - data['parameters']['validation_split']) / 100})",
                "\tgnb = MultinomialNB()",
                "\ty_pred = gnb.fit(X_train, y_train).predict(X_test)",
            ]
        )
    lines.extend(
        [
            "\tincorrect_classified = (y_test != y_pred).sum()",
            "\tprint('*********Summary*************')",
            "\tprint('Total number of instances:\t',X_test.shape[0])",
            "\tprint('Correctly Classified:\t', X_test.shape[0]-incorrect_classified)",
            "\tprint('Incorrectly Classified:\t',incorrect_classified)",
            "\tprint(f'Accuracy:	{round(((X_test.shape[0]-incorrect_classified)/X_test.shape[0])*100, 2)}\\n')",
            "\tprint('*********Classification Report**********')",
            "\tprint(classification_report(y_test, y_pred))",
            "\tprint('*********Confusion Matrix**********')",
            "\tcnf = confusion_matrix(y_test, y_pred)",
            "\tprint('\t'.join([chr(item+97) for item in range(0, len(cnf))]))",
            "\tfor i in range(len(cnf)):",
            "\t\tprint('\t'.join(map(str, cnf[i])),end='  |  ')",
            "\t\tprint(f'{chr(i+97)} = {labels[i]}')",
            "else:",
            "\tprint('Output column must be Nominal not Numerical')",
        ]
    )

    return lines


lines = sys.stdin.readlines()
filepath = lines[0].replace("\n", "")
data = json.loads(lines[1])

lines = [
    "from sklearn.model_selection import train_test_split",
    "from sklearn import preprocessing",
    "import pandas as pd",
]

if data["algo_type"] == "Classification":
    lines = classification_algorithms(data, lines)

temp_path = filepath.split("\\")[:-1]
temp_path.append("model.py")
file_name = "\\".join(temp_path)
print(file_name)

with open(file_name, "w") as file:
    file.write("\n".join(lines))
