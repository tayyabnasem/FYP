from sklearn.model_selection import train_test_split
from sklearn import preprocessing
import pandas as pd
from sklearn.cluster import KMeans
try:
	df = pd.read_csv(r'')
	for column in df.columns.values:
		if df[column].dtypes.name == 'object':
			df[column] = preprocessing.LabelEncoder().fit_transform(df[column])
	train, test = train_test_split(df, test_size=0.3)
	kmeans = KMeans(n_clusters=8, init='random').fit(train)
	cluster_centers = kmeans.cluster_centers_
	columns = df.columns.values
	print('================Cluster Centroids================')
	print('\t\t',end='')
	for column in columns:
		print(column, end='\t')
	for i in range(len(cluster_centers)):
		print(f'\nCluster # {i+1}', end='\t')
		for j in range(len(cluster_centers[i])):
			print('%.3f' % cluster_centers[i][j], end = '\t\t')
	pred = kmeans.predict(test)
	print('\n\n================Clustered Instances================')
	for cluster_num in set(pred):
		count = sum(map(lambda x : x == cluster_num, pred))
		print(f'Cluster : {cluster_num+1}\t{count} ({round(count/len(pred)*100, 2)} %)')
except FileNotFoundError:
	print('Must Upload and Preprocess the dataset first')