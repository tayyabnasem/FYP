import sys, json


def classification_algorithms(data, lines):
    lines.extend(
        [
            "from sklearn.metrics import classification_report",
            "from sklearn.metrics import confusion_matrix",
            "from sklearn.utils import shuffle",
        ]
    )
    if data["algorithm"] == "Random Forest":
        lines.extend(
            [
                "from sklearn.ensemble import RandomForestClassifier",
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
                f"\tclf = RandomForestClassifier(criterion='{data['parameters']['criterion'].lower()}', n_estimators={data['parameters']['trees']})",
                "\ty_pred = clf.fit(X_train, y_train).predict(X_test)",
            ]
        )
    elif data["algorithm"] == "Decision Tree Classifier":
        lines.extend(
            [
                "from sklearn.tree import DecisionTreeClassifier",
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
                f"\tclf = DecisionTreeClassifier(criterion='{data['parameters']['criterion'].lower()}', splitter='{data['parameters']['splitter'].lower()}')",
                "\ty_pred = clf.fit(X_train, y_train).predict(X_test)",
            ]
        )
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
    elif data["algorithm"] == "Categorical Naive Bayes":
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
    elif data["algorithm"] == "Multinomial Naive Bayes":
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
    elif data["algorithm"] == "Bernoulli Naive Bayes":
        lines.extend(
            [
                "from sklearn.naive_bayes import BernoulliNB",
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
                "\tgnb = BernoulliNB()",
                "\ty_pred = gnb.fit(X_train, y_train).predict(X_test)",
            ]
        )
    elif data["algorithm"] == "Logistic Regression":
        lines.extend(
            [
                "from sklearn.linear_model import LogisticRegression",
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
                f"\tclf = LogisticRegression(penalty='{data['parameters']['penalty'].lower()}', max_iter={data['parameters']['max_iter']}, multi_class='{data['parameters']['multi_class'].lower()}', solver='{data['parameters']['solver'].lower()}')",
                "\ty_pred = clf.fit(X_train, y_train).predict(X_test)",
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
            "\tprint(classification_report(y_test, y_pred, zero_division=0))",
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


def regression_algorithms(data, lines):
    lines.extend(
        [
            "from sklearn.utils import shuffle",
            "from sklearn import metrics",
            "from scipy.stats import pearsonr",
            "import numpy as np",
        ]
    )

    if data["algorithm"] == "Linear Regression":
        lines.extend(
            [
                "from sklearn.linear_model import LinearRegression",
                f"df = pd.read_csv(r'{filepath}')",
                "",
                f"if df['{data['parameters']['output_coulmn']}'].dtypes.name != 'object':",
                "\tdf = shuffle(df)",
                "\tfor column in df.columns.values:",
                "\t\tif df[column].dtypes.name == 'object':",
                "\t\t\tdf[column] = preprocessing.LabelEncoder().fit_transform(df[column])",
                f"\tX = df.loc[:, df.columns != '{data['parameters']['output_coulmn']}'].copy()",
                f"\tY = df['{data['parameters']['output_coulmn']}'].copy()",
                f"\tX_train, X_test, y_train, y_test = train_test_split(X, Y, test_size={(100 - data['parameters']['validation_split']) / 100})",
                f"\tregressor = LinearRegression(normalize={data['parameters']['normalize']}, fit_intercept={data['parameters']['fit_intercept']}).fit(X_train, y_train)",
                "\ty_pred = regressor.predict(X_test)",
                "\tcoefficients = regressor.coef_",
                "\tprint('*********Model*************')",
                f"\tprint('Equation: {data['parameters']['output_coulmn']} = ')",
                "\tfor i, column in enumerate(X.columns.values):",
                "\t\tprint(f'{column} * {coefficients[i]} + ')",
                "\tprint(regressor.intercept_, '\\n')",
                "",
            ]
        )
    elif data["algorithm"] == "Decision Tree Regressor":
        lines.extend(
            [
                "from sklearn.tree import DecisionTreeRegressor",
                f"df = pd.read_csv(r'{filepath}')",
                "",
                f"if df['{data['parameters']['output_coulmn']}'].dtypes.name != 'object':",
                "\tdf = shuffle(df)",
                "\tfor column in df.columns.values:",
                "\t\tif df[column].dtypes.name == 'object':",
                "\t\t\tdf[column] = preprocessing.LabelEncoder().fit_transform(df[column])",
                f"\tX = df.loc[:, df.columns != '{data['parameters']['output_coulmn']}'].copy()",
                f"\tY = df['{data['parameters']['output_coulmn']}'].copy()",
                f"\tX_train, X_test, y_train, y_test = train_test_split(X, Y, test_size={(100 - data['parameters']['validation_split']) / 100})",
                f"\tregressor = DecisionTreeRegressor(criterion='{data['parameters']['criterion'].lower()}', splitter='{data['parameters']['splitter'].lower()}').fit(X_train, y_train)",
                "\ty_pred = regressor.predict(X_test)",
            ]
        )
    lines.extend(
        [
            "\tprint('*********Summary*************')",
            "\tcorr, _ = pearsonr(y_test, y_pred)",
            "\tprint('Pearsons correlation: %.3f' % corr)",
            "\tprint('Mean Absolute Error: %.4f' % metrics.mean_absolute_error(y_test, y_pred))",
            "\tprint('Mean Squared Error: %.4f' % metrics.mean_squared_error(y_test, y_pred))",
            "\tprint('Root Mean Squared Error: %.4f' % np.sqrt(metrics.mean_squared_error(y_test, y_pred)))",
            "else:",
            "\tprint('Output column must be Numerical')",
        ]
    )

    return lines


def clustering_algorithms(data, lines):
    if data["algorithm"] == "K-means":
        lines.extend(
            [
                "from sklearn.cluster import KMeans",
                "try:",
                f"\tdf = pd.read_csv(r'{filepath}')",
                "\tfor column in df.columns.values:",
                "\t\tif df[column].dtypes.name == 'object':",
                "\t\t\tdf[column] = preprocessing.LabelEncoder().fit_transform(df[column])",
                f"\ttrain, test = train_test_split(df, test_size={(100 - data['parameters']['validation_split']) / 100})",
                f"\tkmeans = KMeans(n_clusters={data['parameters']['clusters']}, init='{data['parameters']['initial_points'].lower()}'"
                f", max_iter={data['parameters']['max_iter']}, algorithm='{data['parameters']['algorithm'].lower()}').fit(train)",
                "\tcluster_centers = kmeans.cluster_centers_",
                "\tcolumns = df.columns.values",
                "\tprint('Number of Iterations: ',kmeans.n_iter_)",
                "\tprint('Sum of Squared Errors: ',kmeans.inertia_,'\\n')",
                "\tprint('================Cluster Centroids================')",
                "\tprint('\\t\\t',end='')",
                "\tfor column in columns:",
                "\t\tprint(column, end='\\t')",
                "\tfor i in range(len(cluster_centers)):",
                "\t\tprint(f'\\nCluster # {i+1}', end='\\t')",
                "\t\tfor j in range(len(cluster_centers[i])):",
                "\t\t\tprint('%.3f' % cluster_centers[i][j], end = '\\t\\t')",
                "\tpred = kmeans.predict(test)",
                "\tprint('\\n\\n================Clustered Instances================')",
                "\tfor cluster_num in set(pred):",
                "\t\tcount = sum(map(lambda x : x == cluster_num, pred))",
                "\t\tprint(f'Cluster : {cluster_num+1}\\t{count} ({round(count/len(pred)*100, 2)} %)')",
                "except FileNotFoundError:",
                "\tprint('Must Upload and Preprocess the dataset first')",
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
elif data["algo_type"] == "Regression":
    lines = regression_algorithms(data, lines)
else:
    lines = clustering_algorithms(data, lines)

temp_path = filepath.split("\\")[:-1]
temp_path.append("model.py")
file_name = "\\".join(temp_path)
print(file_name)

with open(file_name, "w") as file:
    file.write("\n".join(lines))
