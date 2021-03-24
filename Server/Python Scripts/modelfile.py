import pandas as pd
import numpy as np

from tensorflow.keras import losses



losses.BinaryCrossentropy()

df = pd.read_csv("C:\\Users\\User\\Downloads\\archive (1)\weatherAUS.csv")
#"C:\Users\User\Downloads\archive (1)\weatherAUS.csv"

labels = df['RainToday'].unique().tolist()
labels = list(filter(lambda label: not label is np.nan, labels))

print(labels)
print(df['RainToday'].value_counts())

print(df['RainToday'].value_counts().values)

print(df['RainToday'].isnull().sum())

print(df['RainToday'].shape)