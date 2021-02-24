import sys, json

data = sys.stdin.readlines()
model_layers = json.loads(data[0])

lines = ["from tensorflow import keras",
         "from tensorflow.keras import layers", "",
         "model = keras.Sequential()"]

for layer in model_layers:
    layer_str = f"model.add(layers.{layer['layerName']}(units={layer['units']}"
    if layer['activationFunction'] != 'default':
        layer_str += f", activation='{layer['activationFunction']}'))"
    else:
        layer_str += f"))"

    lines.append( layer_str)
        #f"model.add(layers.{layer['layerName']}(units={layer['units']}, activation='{layer['activationFunction']}'))")
    with open('temp.py', 'w') as file:
        file.write("\n".join(lines))