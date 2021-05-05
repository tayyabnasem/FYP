from sklearn.model_selection import train_test_split
import pandas as pd
from sklearn.naive_bayes import GaussianNB
df = pd.read_csv(r'undefined')

X = df.loc[:, df.columns != 'species']
Y = df['species']

X_train, X_test, y_train, y_test = train_test_split(X, Y, test_size=0.3, random_state=42)
gnb = GaussianNB()
y_pred = gnb.fit(X_train, y_train).predict(X_test)
print(classification_report(y_test, y_pred)