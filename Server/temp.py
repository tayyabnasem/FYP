from tensorflow import keras
from tensorflow.keras import layers
from tensorflow.keras import losses
from tensorflow.keras import optimizers
import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
df = pd.read_csv(r'Uploads\uploadstayyabnasem@gmail.com\IRIS_preprocessed.csv')

X = df.loc[:, df.columns != 'species']
Y = df['species']

X_train, X_test, y_train, y_test = train_test_split(X, Y, test_size=0.3, random_state=42)
model = keras.Sequential()

model.add(layers.Dense(units=10, activation='relu'))
model.add(layers.Dense(units=5, activation='relu'))
model.add(layers.Dense(units=3, activation='softmax'))

opt = optimizers.Adam(learning_rate=0.01)
loss = losses.BinaryCrossentropy()
model.compile(loss = loss, optimizer = opt, metrics = ['accuracy'])
model.fit(X_train, y_train, batch_size=16, epochs=50)