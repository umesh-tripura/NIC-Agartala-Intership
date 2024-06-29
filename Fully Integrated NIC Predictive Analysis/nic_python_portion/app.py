from flask import Flask, jsonify
import pandas as pd
from statsmodels.tsa.arima.model import ARIMA
import matplotlib.pyplot as plt
import seaborn as sns
import requests
import psycopg2
from psycopg2 import sql

app = Flask(__name__)
csv_file = './updated_Nic_test data.csv'  # Update with your CSV file path
api_url = "http://localhost:8080/auth/fetch-nic"  # Replace with your API endpoint
response = requests.get(api_url)
json_data = response.json()
df1=pd.DataFrame(json_data)
df1.to_csv(csv_file, mode='a', index=False, header=False)


# PostgreSQL connection parameters
db_params = {
    'dbname': 'NIC_Predictive_Analysis',
    'user': 'postgres',
    'password': 'Agartala',
    'host': 'localhost',
    'port': '5432'
}


def load_data(filename):
    try:
        df = pd.read_csv(filename)
        return df
    except Exception as e:
        print(f"Error loading CSV file: {e}")
        return None

def preprocess_data(df):
    try:
        # Parse dates with format '%d-%m-%Y'
        df['Demand_date'] = pd.to_datetime(df['Demand_date'], format='%Y-%m-%d')
    except Exception as e:
        print(f"Error parsing dates: {e}")
        return None
    df.set_index('Demand_date', inplace=True)
    df.sort_index(inplace=True)
    return df

def build_arima_model(series, order=(5, 1, 0)):
    model = ARIMA(series, order=order)
    model_fit = model.fit()
    return model_fit

def forecast_sales(model, steps):
    forecast = model.forecast(steps=steps)
    return forecast

def save_forecast_to_db(forecast, start_date):
    try:
        conn = psycopg2.connect(**db_params)
        cur = conn.cursor()
        insert_query = sql.SQL("""
            INSERT INTO forecast_results (forecast_date, forecasted_qty)
            VALUES (%s, %s)
        """)
        for i, qty in enumerate(forecast):
            forecast_date = start_date + pd.DateOffset(months=i)
            cur.execute(insert_query, (forecast_date, qty))
        conn.commit()
        cur.close()
        conn.close()
    except Exception as e:
        print(f"Error saving to database: {e}")

@app.route('/api/forecast', methods=['GET'])
def get_forecast():
    try:
        df = load_data(csv_file)
        if df is None:
            return jsonify({'error': 'Failed to load data'}), 500
        
        df = preprocess_data(df)
        if df is None:
            return jsonify({'error': 'Failed to preprocess data'}), 500
        
        series = df['Demand_qty']
        
        model = build_arima_model(series)
        
        # Forecast 12 steps ahead (adjust as needed)
        forecast_steps = 12
        forecast = forecast_sales(model, steps=forecast_steps)
        
        # Save forecast to database
        save_forecast_to_db(forecast, series.index[-1])
        
        # Plotting the forecast
        sns.set(style="whitegrid")
        plt.figure(figsize=(14, 7))
        plt.plot(series.index, series.values, label='Demand Qty', color='blue', linestyle='-', linewidth=2)
        plt.plot(pd.date_range(start=series.index[-1], periods=forecast_steps, freq='M'), forecast, label='Forecasted Qty', color='red', linestyle='--', linewidth=2)
        plt.title('Forecasted Qty', fontsize=16)
        plt.xlabel('Date', fontsize=14)
        plt.ylabel('Demand_qty', fontsize=14)
        plt.legend(fontsize=12)
        plt.tight_layout()
        
        # Show plot in a window
        plt.show()
        
        # Convert forecast to list for JSON serialization
        forecast_data = {
            'forecasted_sales': forecast.tolist()
        }
        
        return jsonify(forecast_data)
    except Exception as e:
        return jsonify({'error': f"An error occurred: {e}"}), 500

@app.route('/', methods=['GET'])
def index():
    return "Welcome to my Flask API! Navigate to http://127.0.0.1:5000/api/forecast"

if __name__ == '__main__':
    app.run(debug=True)