import sys, json

from tensorflow.python.keras.layers.recurrent import LSTM


def create_dense_layer(units, activation_function="default", input_dim=None):
    layer_str = f"model.add(layers.Dense(units={units}"
    if activation_function != "default":
        layer_str += f", activation='{activation_function}'))"
    else:
        layer_str += f"))"
    return layer_str


def create_dropout_layer(dropout_rate):
    layer_str = f"model.add(layers.Dropout(rate={dropout_rate}))"
    return layer_str


def create_lstm_layer(units, activation, recc_activation, dropout, return_sequence):
    layer_str = f"model.add(layers.LSTM(units={units}, activation='{activation}', recurrent_activation='{recc_activation}', dropout={dropout}, return_sequences=True))"
    return layer_str


lines = sys.stdin.readlines()
filepath = lines[0].replace("\n", "")
data = json.loads(lines[1])

model_layers = data["layers"]
hyperparameters = data["hyperparameters"]


lines = [
    "from tensorflow import keras",
    "from tensorflow.keras import layers",
    "from tensorflow.keras import losses",
    "from tensorflow.keras import optimizers",
    "from tensorflow.keras.utils import to_categorical",
    "from matplotlib import pyplot as plt",
    "from sklearn import preprocessing",
    "import pandas as pd",
    "import numpy as np",
    "import time",
    "from sklearn.model_selection import train_test_split",
    f"df = pd.read_csv(r'{filepath}')",
    "",
    f"X = df.loc[:, df.columns != '{hyperparameters['output_coulmn']}']",
    f"Y = df['{hyperparameters['output_coulmn']}']",
    "",
    "for column in X.columns.values:",
    "\tif X[column].dtypes.name == 'object':",
    "\t\tX[column] = preprocessing.LabelEncoder().fit_transform(X[column])",
    "Y = preprocessing.LabelEncoder().fit_transform(Y)",
]
if hyperparameters["categorize_output"] == "Yes":
    lines.append("Y = to_categorical(Y)")

lines.extend(
    [
        f"X_train, X_test, y_train, y_test = train_test_split(X, Y, test_size={(100 - hyperparameters['validation_split']) / 100}, random_state=42)",
        "model = keras.Sequential()",
        "",
    ]
)

LSTM_used = False
for i, layer in enumerate(model_layers):
        if layer["layerName"] == "Dense":
            lines.append(
                create_dense_layer(
                    layer["units"],
                    layer["activationFunction"].lower(),
                )
            )
        elif layer["layerName"] == "Dropout":
            lines.append(create_dropout_layer(layer["dropoutRate"]))
        elif layer["layerName"] == "LSTM":
            lines.append(
                create_lstm_layer(
                    layer["units"],
                    layer["activationFunction"].lower(),
                    layer["recurrentActivation"].lower(),
                    layer["dropout"],
                    layer["returnSequence"],
                )
            )
            LSTM_used = True

images_path = filepath.split("\\")[:-1]
model_path = images_path.copy()
images_path = "/".join(images_path)

if hyperparameters["categorize_output"] == "Yes":
    lines.append(create_dense_layer("Y.shape[1]", "sigmoid"))
else:
    lines.append(create_dense_layer(1, "sigmoid"))

if LSTM_used:
    lines.extend(
        [
            "if len(X_train.shape)>1:",
            "\tX_train = np.array(X_train).reshape(-1, 1, X_train.shape[1])",
            "\tX_test = np.array(X_test).reshape(-1, 1, X_test.shape[1])",
            "else: ",
            "\tX_train = np.array(X_train).reshape(-1, 1, 1)",
            "\tX_test = np.array(X_test).reshape(-1, 1, 1)",
            "if len(y_train.shape)>1:",
            "\ty_train = np.array(y_train.reshape(-1, 1, y_train.shape[1]))",
            "\ty_test = np.array(y_test.reshape(-1, 1, y_test.shape[1]))",
            "else:",
            "\ty_train = np.array(y_train.reshape(-1, 1, 1))",
            "\ty_test = np.array(y_test.reshape(-1, 1, 1))",
        ]
    )

lines.extend(
    [
        "",
        f"opt = optimizers.{hyperparameters['optimizer']}(learning_rate={hyperparameters['learningRate']})",
        f"loss = losses.{hyperparameters['lossFunction'].replace(' ', '')}()",
        f"model.compile(loss = loss, optimizer = opt, metrics = ['accuracy'])",
        f"history = model.fit(X_train, y_train, batch_size={hyperparameters['batchSize']}, epochs={hyperparameters['epoch']}, validation_data=(X_test, y_test))",
        f"trained_model = '{images_path}/model.h5'",
        f"model.save(trained_model)",
        "print(trained_model, flush=True)",
        "print('Training Complete...', flush=True)",
        "plt.plot(history.history['accuracy'])",
        "plt.plot(history.history['val_accuracy'])",
        "plt.title('Model Accuracy')",
        "plt.ylabel('Accuracy')",
        "plt.xlabel('Epoch')",
        "plt.legend(['Train', 'Test'], loc='upper left')",
        f"filename = '{images_path}/plot'+str(round(time.time() * 1000))+'.png'",
        "print(f'Model Accuracy: {filename}')\n",
        "plt.savefig(filename)",
        "plt.clf()",
        "plt.plot(history.history['loss'])",
        "plt.plot(history.history['val_loss'])",
        "plt.title('Model Loss')",
        "plt.ylabel('Loss')",
        "plt.xlabel('Epoch')",
        "plt.legend(['Train', 'Test'], loc='upper left')",
        f"filename = '{images_path}/plot'+str(round(time.time() * 1000))+'.png'",
        "print(f'Model Loss: {filename}')",
        "plt.savefig(filename)",
    ]
)

model_path.append("model.py")
file_name = "/".join(model_path)
print(file_name)

with open(file_name, "w") as file:
    file.write("\n".join(lines))
