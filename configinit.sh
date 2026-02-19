kubectl create namespace ecommerce
kubectl create configmap postgres-init-script \
  --from-file=init-database.sql=./sql/init-database.sql \
  --namespace=ecommerce
