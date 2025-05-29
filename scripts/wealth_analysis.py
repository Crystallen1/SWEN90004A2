import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
from scipy.stats import pearsonr
import seaborn as sns
import os

# Set font for better display
plt.rcParams['font.sans-serif'] = ['Arial', 'DejaVu Sans']
plt.rcParams['axes.unicode_minus'] = False

# Define paths
data_dir = '../data/output/'
plots_dir = '../data/plots/'

# Create plots directory if it doesn't exist
os.makedirs(plots_dir, exist_ok=True)

# Read data with correct paths
wealth_data = pd.read_csv(os.path.join(data_dir, 'wealth_simulation_results.csv'))
class_plot_data = pd.read_csv(os.path.join(data_dir, 'class-plot (3).csv'))
gini_data = pd.read_csv(os.path.join(data_dir, 'gini-index-v-time (2).csv'))

# Create figures
fig, (ax1, ax2) = plt.subplots(2, 1, figsize=(12, 10))

# Plot 1: Three class population changes
ax1.plot(wealth_data['Round'], wealth_data['Poor_Count'], label='Poor Count', linewidth=2, color='red')
ax1.plot(wealth_data['Round'], wealth_data['Middle_Count'], label='Middle Class Count', linewidth=2, color='blue')
ax1.plot(wealth_data['Round'], wealth_data['Rich_Count'], label='Rich Count', linewidth=2, color='green')

ax1.set_xlabel('Round')
ax1.set_ylabel('Population Count')
ax1.set_title('Wealth Class Population Trends')
ax1.legend()
ax1.grid(True, alpha=0.3)

# Plot 2: Gini coefficient changes
ax2.plot(wealth_data['Round'], wealth_data['Gini_Coefficient'], label='Gini Coefficient', linewidth=2, color='purple')
ax2.set_xlabel('Round')
ax2.set_ylabel('Gini Coefficient')
ax2.set_title('Gini Coefficient Trend')
ax2.legend()
ax2.grid(True, alpha=0.3)

plt.tight_layout()
plt.savefig(os.path.join(plots_dir, 'wealth_simulation_plots.png'), dpi=300, bbox_inches='tight')
plt.show()

# Calculate correlation coefficients
print("=== Correlation Analysis ===\n")

# 1. Compare with class-plot data
print("1. Correlation with class-plot data:")
# Ensure consistent data length, take the shorter length
min_len = min(len(wealth_data), len(class_plot_data))

# Poor population correlation
poor_corr, poor_p = pearsonr(wealth_data['Poor_Count'][:min_len], class_plot_data['low'][:min_len])
print(f"   Poor Count Correlation: {poor_corr:.4f} (p-value: {poor_p:.4f})")

# Middle class correlation
middle_corr, middle_p = pearsonr(wealth_data['Middle_Count'][:min_len], class_plot_data['mid'][:min_len])
print(f"   Middle Class Correlation: {middle_corr:.4f} (p-value: {middle_p:.4f})")

# Rich population correlation
rich_corr, rich_p = pearsonr(wealth_data['Rich_Count'][:min_len], class_plot_data['up'][:min_len])
print(f"   Rich Count Correlation: {rich_corr:.4f} (p-value: {rich_p:.4f})")

# 2. Compare with gini-index data
print("\n2. Correlation with gini-index data:")
min_len_gini = min(len(wealth_data), len(gini_data))

gini_corr, gini_p = pearsonr(wealth_data['Gini_Coefficient'][:min_len_gini], gini_data['default'][:min_len_gini])
print(f"   Gini Coefficient Correlation: {gini_corr:.4f} (p-value: {gini_p:.4f})")

# Create correlation heatmap
print("\n=== Detailed Correlation Analysis ===")

# Prepare data for correlation analysis
correlation_data = pd.DataFrame({
    'My_Poor': wealth_data['Poor_Count'][:min_len],
    'My_Middle': wealth_data['Middle_Count'][:min_len],
    'My_Rich': wealth_data['Rich_Count'][:min_len],
    'My_Gini': wealth_data['Gini_Coefficient'][:min_len_gini],
    'Ref_Low': class_plot_data['low'][:min_len],
    'Ref_Mid': class_plot_data['mid'][:min_len],
    'Ref_Up': class_plot_data['up'][:min_len],
    'Ref_Gini': gini_data['default'][:min_len_gini]
})

# Calculate correlation matrix
corr_matrix = correlation_data.corr()

# Print key correlations
print("\nKey Correlations:")
print(f"My Poor Count vs Reference Poor Count: {corr_matrix.loc['My_Poor', 'Ref_Low']:.4f}")
print(f"My Middle Class vs Reference Middle Class: {corr_matrix.loc['My_Middle', 'Ref_Mid']:.4f}")
print(f"My Rich Count vs Reference Rich Count: {corr_matrix.loc['My_Rich', 'Ref_Up']:.4f}")
print(f"My Gini Coefficient vs Reference Gini: {corr_matrix.loc['My_Gini', 'Ref_Gini']:.4f}")

# Calculate Root Mean Square Error (RMSE)
def calculate_rmse(actual, predicted):
    return np.sqrt(np.mean((actual - predicted) ** 2))

print("\n=== Root Mean Square Error (RMSE) Analysis ===")
print(f"Poor Count RMSE: {calculate_rmse(wealth_data['Poor_Count'][:min_len], class_plot_data['low'][:min_len]):.2f}")
print(f"Middle Class RMSE: {calculate_rmse(wealth_data['Middle_Count'][:min_len], class_plot_data['mid'][:min_len]):.2f}")
print(f"Rich Count RMSE: {calculate_rmse(wealth_data['Rich_Count'][:min_len], class_plot_data['up'][:min_len]):.2f}")
print(f"Gini Coefficient RMSE: {calculate_rmse(wealth_data['Gini_Coefficient'][:min_len_gini], gini_data['default'][:min_len_gini]):.4f}")

# Create comparison plots
fig, axes = plt.subplots(2, 2, figsize=(15, 12))

# Poor count comparison
axes[0,0].plot(wealth_data['Round'][:min_len], wealth_data['Poor_Count'][:min_len], 
               label='My Simulation', linewidth=2, color='red')
axes[0,0].plot(class_plot_data['Time'][:min_len], class_plot_data['low'][:min_len], 
               label='Reference Data', linewidth=2, color='red', linestyle='--', alpha=0.7)
axes[0,0].set_title('Poor Count Comparison')
axes[0,0].set_xlabel('Time/Round')
axes[0,0].set_ylabel('Population Count')
axes[0,0].legend()
axes[0,0].grid(True, alpha=0.3)

# Middle class comparison
axes[0,1].plot(wealth_data['Round'][:min_len], wealth_data['Middle_Count'][:min_len], 
               label='My Simulation', linewidth=2, color='blue')
axes[0,1].plot(class_plot_data['Time'][:min_len], class_plot_data['mid'][:min_len], 
               label='Reference Data', linewidth=2, color='blue', linestyle='--', alpha=0.7)
axes[0,1].set_title('Middle Class Count Comparison')
axes[0,1].set_xlabel('Time/Round')
axes[0,1].set_ylabel('Population Count')
axes[0,1].legend()
axes[0,1].grid(True, alpha=0.3)

# Rich count comparison
axes[1,0].plot(wealth_data['Round'][:min_len], wealth_data['Rich_Count'][:min_len], 
               label='My Simulation', linewidth=2, color='green')
axes[1,0].plot(class_plot_data['Time'][:min_len], class_plot_data['up'][:min_len], 
               label='Reference Data', linewidth=2, color='green', linestyle='--', alpha=0.7)
axes[1,0].set_title('Rich Count Comparison')
axes[1,0].set_xlabel('Time/Round')
axes[1,0].set_ylabel('Population Count')
axes[1,0].legend()
axes[1,0].grid(True, alpha=0.3)

# Gini coefficient comparison
axes[1,1].plot(wealth_data['Round'][:min_len_gini], wealth_data['Gini_Coefficient'][:min_len_gini], 
               label='My Simulation', linewidth=2, color='purple')
axes[1,1].plot(gini_data['Time'][:min_len_gini], gini_data['default'][:min_len_gini], 
               label='Reference Data', linewidth=2, color='purple', linestyle='--', alpha=0.7)
axes[1,1].set_title('Gini Coefficient Comparison')
axes[1,1].set_xlabel('Time/Round')
axes[1,1].set_ylabel('Gini Coefficient')
axes[1,1].legend()
axes[1,1].grid(True, alpha=0.3)

plt.tight_layout()
plt.savefig(os.path.join(plots_dir, 'comparison_plots.png'), dpi=300, bbox_inches='tight')
plt.show()

print("\nAnalysis Complete! Generated image files:")
print(f"- {os.path.join(plots_dir, 'wealth_simulation_plots.png')}: Wealth class and Gini coefficient trends")
print(f"- {os.path.join(plots_dir, 'comparison_plots.png')}: Comparison with reference data")
print(f"\nAll files saved to: {os.path.abspath(plots_dir)}") 