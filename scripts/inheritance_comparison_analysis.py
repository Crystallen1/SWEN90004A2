import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
from scipy.stats import pearsonr, ttest_ind
import seaborn as sns
import os

# Set font and style
plt.rcParams['font.sans-serif'] = ['Arial', 'DejaVu Sans']
plt.rcParams['axes.unicode_minus'] = False

# Define paths
data_dir = '../data/output/'
plots_dir = '../data/plots/'

# Create plots directory
os.makedirs(plots_dir, exist_ok=True)

# Read data
inheritance_data = pd.read_csv(os.path.join(data_dir, 'inheritance_comparison_results.csv'))

print("=== Inheritance Mechanism Comparison Analysis ===")
print(f"Total data rows: {len(inheritance_data)}")
print(f"Model types: {inheritance_data['Model_Type'].unique()}")
print(f"Round range: {inheritance_data['Round'].min()} - {inheritance_data['Round'].max()}")

# Separate data for two models
inheritance_model = inheritance_data[inheritance_data['Model_Type'] == 'Inheritance']
no_inheritance_model = inheritance_data[inheritance_data['Model_Type'] == 'Baseline']

# Create main comparison charts (similar to wealth_analysis.py)
fig, (ax1, ax2) = plt.subplots(2, 1, figsize=(12, 10))

# Plot 1: Three class population changes comparison
ax1.plot(inheritance_model['Round'], inheritance_model['Poor_Count'], 
         label='Poor (Inheritance)', linewidth=2, color='red')
ax1.plot(inheritance_model['Round'], inheritance_model['Middle_Count'], 
         label='Middle (Inheritance)', linewidth=2, color='blue')
ax1.plot(inheritance_model['Round'], inheritance_model['Rich_Count'], 
         label='Rich (Inheritance)', linewidth=2, color='green')

ax1.plot(no_inheritance_model['Round'], no_inheritance_model['Poor_Count'], 
         label='Poor (Baseline)', linewidth=2, color='red', linestyle='--', alpha=0.7)
ax1.plot(no_inheritance_model['Round'], no_inheritance_model['Middle_Count'], 
         label='Middle (Baseline)', linewidth=2, color='blue', linestyle='--', alpha=0.7)
ax1.plot(no_inheritance_model['Round'], no_inheritance_model['Rich_Count'], 
         label='Rich (Baseline)', linewidth=2, color='green', linestyle='--', alpha=0.7)

ax1.set_xlabel('Round')
ax1.set_ylabel('Population Count')
ax1.set_title('Wealth Class Population Trends - Inheritance vs Baseline')
ax1.legend()
ax1.grid(True, alpha=0.3)

# Plot 2: Gini coefficient comparison
ax2.plot(inheritance_model['Round'], inheritance_model['Gini_Coefficient'], 
         label='Inheritance', linewidth=2, color='red')
ax2.plot(no_inheritance_model['Round'], no_inheritance_model['Gini_Coefficient'], 
         label='Baseline', linewidth=2, color='blue', linestyle='--')

ax2.set_xlabel('Round')
ax2.set_ylabel('Gini Coefficient')
ax2.set_title('Gini Coefficient Comparison - Inheritance vs Baseline')
ax2.legend()
ax2.grid(True, alpha=0.3)

plt.tight_layout()
plt.savefig(os.path.join(plots_dir, 'inheritance_comparison_plots.png'), dpi=300, bbox_inches='tight')
plt.show()

# Create detailed comparison plots (similar to wealth_analysis.py comparison style)
fig, axes = plt.subplots(2, 2, figsize=(15, 12))

# Poor count comparison
axes[0,0].plot(inheritance_model['Round'], inheritance_model['Poor_Count'], 
               label='Inheritance', linewidth=2, color='red')
axes[0,0].plot(no_inheritance_model['Round'], no_inheritance_model['Poor_Count'], 
               label='Baseline', linewidth=2, color='blue', linestyle='--', alpha=0.7)
axes[0,0].set_title('Poor Population Comparison')
axes[0,0].set_xlabel('Round')
axes[0,0].set_ylabel('Poor Count')
axes[0,0].legend()
axes[0,0].grid(True, alpha=0.3)

# Middle class comparison
axes[0,1].plot(inheritance_model['Round'], inheritance_model['Middle_Count'], 
               label='Inheritance', linewidth=2, color='red')
axes[0,1].plot(no_inheritance_model['Round'], no_inheritance_model['Middle_Count'], 
               label='Baseline', linewidth=2, color='blue', linestyle='--', alpha=0.7)
axes[0,1].set_title('Middle Class Population Comparison')
axes[0,1].set_xlabel('Round')
axes[0,1].set_ylabel('Middle Class Count')
axes[0,1].legend()
axes[0,1].grid(True, alpha=0.3)

# Rich count comparison
axes[1,0].plot(inheritance_model['Round'], inheritance_model['Rich_Count'], 
               label='Inheritance', linewidth=2, color='red')
axes[1,0].plot(no_inheritance_model['Round'], no_inheritance_model['Rich_Count'], 
               label='Baseline', linewidth=2, color='blue', linestyle='--', alpha=0.7)
axes[1,0].set_title('Rich Population Comparison')
axes[1,0].set_xlabel('Round')
axes[1,0].set_ylabel('Rich Count')
axes[1,0].legend()
axes[1,0].grid(True, alpha=0.3)

# Average wealth comparison
axes[1,1].plot(inheritance_model['Round'], inheritance_model['Avg_Wealth'], 
               label='Inheritance', linewidth=2, color='red')
axes[1,1].plot(no_inheritance_model['Round'], no_inheritance_model['Avg_Wealth'], 
               label='Baseline', linewidth=2, color='blue', linestyle='--', alpha=0.7)
axes[1,1].set_title('Average Wealth Comparison')
axes[1,1].set_xlabel('Round')
axes[1,1].set_ylabel('Average Wealth')
axes[1,1].legend()
axes[1,1].grid(True, alpha=0.3)

plt.tight_layout()
plt.savefig(os.path.join(plots_dir, 'inheritance_detailed_comparison.png'), dpi=300, bbox_inches='tight')
plt.show()

# Calculate correlation and statistical analysis
print("\n=== Correlation Analysis ===")

# Calculate correlation between inheritance and no inheritance models
metrics = ['Poor_Count', 'Middle_Count', 'Rich_Count', 'Gini_Coefficient', 'Avg_Wealth']

for metric in metrics:
    corr, p_value = pearsonr(inheritance_model[metric], no_inheritance_model[metric])
    print(f"{metric} correlation between models: {corr:.4f} (p-value: {p_value:.4f})")

# Perform statistical tests
print("\n=== Statistical Test Results ===")

# T-test for Gini coefficient
gini_with = inheritance_model['Gini_Coefficient']
gini_without = no_inheritance_model['Gini_Coefficient']
t_stat_gini, p_value_gini = ttest_ind(gini_with, gini_without)
print(f"Gini Coefficient T-test:")
print(f"  T-statistic: {t_stat_gini:.4f}")
print(f"  P-value: {p_value_gini:.4f}")
print(f"  Significant difference: {'Yes' if p_value_gini < 0.05 else 'No'}")

# T-test for average wealth
wealth_with = inheritance_model['Avg_Wealth']
wealth_without = no_inheritance_model['Avg_Wealth']
t_stat_wealth, p_value_wealth = ttest_ind(wealth_with, wealth_without)
print(f"\nAverage Wealth T-test:")
print(f"  T-statistic: {t_stat_wealth:.4f}")
print(f"  P-value: {p_value_wealth:.4f}")
print(f"  Significant difference: {'Yes' if p_value_wealth < 0.05 else 'No'}")

# Calculate key metrics
print("\n=== Key Performance Metrics ===")

# With inheritance model
with_initial_poverty = inheritance_model.iloc[0]['Poor_Count'] / inheritance_model.iloc[0]['Population']
with_final_poverty = inheritance_model.iloc[-1]['Poor_Count'] / inheritance_model.iloc[-1]['Population']
with_poverty_change = (with_final_poverty - with_initial_poverty) * 100

with_initial_gini = inheritance_model.iloc[0]['Gini_Coefficient']
with_final_gini = inheritance_model.iloc[-1]['Gini_Coefficient']
with_gini_change = with_final_gini - with_initial_gini

print(f"\nInheritance Model:")
print(f"  Poverty rate change: {with_poverty_change:+.2f}%")
print(f"  Gini coefficient change: {with_gini_change:+.4f}")
print(f"  Final average wealth: {inheritance_model.iloc[-1]['Avg_Wealth']:.2f}")
print(f"  Wealth volatility: {inheritance_model['Avg_Wealth'].std():.2f}")

# Without inheritance model (baseline)
without_initial_poverty = no_inheritance_model.iloc[0]['Poor_Count'] / no_inheritance_model.iloc[0]['Population']
without_final_poverty = no_inheritance_model.iloc[-1]['Poor_Count'] / no_inheritance_model.iloc[-1]['Population']
without_poverty_change = (without_final_poverty - without_initial_poverty) * 100

without_initial_gini = no_inheritance_model.iloc[0]['Gini_Coefficient']
without_final_gini = no_inheritance_model.iloc[-1]['Gini_Coefficient']
without_gini_change = without_final_gini - without_initial_gini

print(f"\nBaseline Model:")
print(f"  Poverty rate change: {without_poverty_change:+.2f}%")
print(f"  Gini coefficient change: {without_gini_change:+.4f}")
print(f"  Final average wealth: {no_inheritance_model.iloc[-1]['Avg_Wealth']:.2f}")
print(f"  Wealth volatility: {no_inheritance_model['Avg_Wealth'].std():.2f}")

# Calculate differences
gini_diff = inheritance_model['Gini_Coefficient'] - no_inheritance_model['Gini_Coefficient']
wealth_diff = inheritance_model['Avg_Wealth'] - no_inheritance_model['Avg_Wealth']

print(f"\nInheritance Impact Analysis:")
print(f"  Average Gini difference: {np.mean(gini_diff):.4f}")
print(f"  Average wealth difference: {np.mean(wealth_diff):.2f}")
print(f"  Poverty rate difference: {with_final_poverty - without_final_poverty:.4f}")

print(f"\nAnalysis complete! Generated chart files:")
print(f"- {os.path.join(plots_dir, 'inheritance_comparison_plots.png')}: Main inheritance comparison trends")
print(f"- {os.path.join(plots_dir, 'inheritance_detailed_comparison.png')}: Detailed inheritance comparison")
print(f"\nAll files saved to: {os.path.abspath(plots_dir)}") 