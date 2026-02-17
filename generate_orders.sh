#!/bin/bash

BASE_URL="http://localhost:8080/api/v1/orders"

PRODUCT_NAMES=("Багет" "Круассан" "Чиабатта" "Ржаной хлеб" "Булочка с корицей" "Пирожок с яблоком" "Эклер" "Наполеон" "Медовик" "Тарталетка")
PRODUCT_DESCS=("Свежая выпечка" "Классический рецепт" "Авторский рецепт" "Домашний" "Фирменный" "По-итальянски" "Французский" "С начинкой" "Хрустящий" "Нежный")

for i in $(seq 1 100); do
  PRODUCTS=""
  for j in $(seq 1 5); do
    NAME=${PRODUCT_NAMES[$((RANDOM % ${#PRODUCT_NAMES[@]}))]}
    DESC=${PRODUCT_DESCS[$((RANDOM % ${#PRODUCT_DESCS[@]}))]}
    PRICE=$(( RANDOM % 500 + 50 ))
    QTY=$(( RANDOM % 10 + 1 ))

    PRODUCT="{\"name\":\"$NAME\",\"description\":\"$DESC\",\"price\":$PRICE,\"quantity\":$QTY}"

    if [ "$j" -eq 1 ]; then
      PRODUCTS="$PRODUCT"
    else
      PRODUCTS="$PRODUCTS,$PRODUCT"
    fi
  done

  STATUS="NEW"

  BODY="{\"product\":[$PRODUCTS],\"status\":\"$STATUS\"}"

  echo "Creating order $i..."
  curl -s -o /dev/null -w "Order $i: HTTP %{http_code}\n" \
    -X POST "$BASE_URL" \
    -H "Content-Type: application/json" \
    -d "$BODY"
done

echo "Done. 100 orders created."
