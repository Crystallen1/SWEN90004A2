import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
from scipy.stats import pearsonr
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
model_data = pd.read_csv(os.path.join(data_dir, 'model_comparison_results.csv'))

print("=== Model Comparison Analysis ===")
print(f"Total data rows: {len(model_data)}")
print(f"Model types: {model_data['Model_Type'].unique()}")
print(f"Round range: {model_data['Round'].min()} - {model_data['Round'].max()}")

# Separate data for three models
baseline_model = model_data[model_data['Model_Type'] == 'Baseline']
tax_model = model_data[model_data['Model_Type'] == 'Tax']
spreading_model = model_data[model_data['Model_Type'] == 'Spreading']

# Create main comparison charts (similar to wealth_analysis.py)
fig, (ax1, ax2) = plt.subplots(2, 1, figsize=(12, 10))

# Plot 1: Three class population changes comparison
ax1.plot(baseline_model['Round'], baseline_model['Poor_Count'], 
         label='Poor (Baseline)', linewidth=2, color='red')
ax1.plot(baseline_model['Round'], baseline_model['Middle_Count'], 
         label='Middle (Baseline)', linewidth=2, color='blue')
ax1.plot(baseline_model['Round'], baseline_model['Rich_Count'], 
         label='Rich (Baseline)', linewidth=2, color='green')

ax1.plot(tax_model['Round'], tax_model['Poor_Count'], 
         label='Poor (Tax)', linewidth=2, color='red', linestyle='--', alpha=0.7)
ax1.plot(tax_model['Round'], tax_model['Middle_Count'], 
         label='Middle (Tax)', linewidth=2, color='blue', linestyle='--', alpha=0.7)
ax1.plot(tax_model['Round'], tax_model['Rich_Count'], 
         label='Rich (Tax)', linewidth=2, color='green', linestyle='--', alpha=0.7)

ax1.plot(spreading_model['Round'], spreading_model['Poor_Count'], 
         label='Poor (Spreading)', linewidth=2, color='red', linestyle=':', alpha=0.7)
ax1.plot(spreading_model['Round'], spreading_model['Middle_Count'], 
         label='Middle (Spreading)', linewidth=2, color='blue', linestyle=':', alpha=0.7)
ax1.plot(spreading_model['Round'], spreading_model['Rich_Count'], 
         label='Rich (Spreading)', linewidth=2, color='green', linestyle=':', alpha=0.7)

ax1.set_xlabel('Round')
ax1.set_ylabel('Population Count')
ax1.set_title('Wealth Class Population Trends - Three Model Comparison')
ax1.legend(bbox_to_anchor=(1.05, 1), loc='upper left')
ax1.grid(True, alpha=0.3)

# Plot 2: Gini coefficient comparison
ax2.plot(baseline_model['Round'], baseline_model['Gini_Coefficient'], 
         label='Baseline', linewidth=2, color='red')
ax2.plot(tax_model['Round'], tax_model['Gini_Coefficient'], 
         label='Tax', linewidth=2, color='blue', linestyle='--')
ax2.plot(spreading_model['Round'], spreading_model['Gini_Coefficient'], 
         label='Spreading', linewidth=2, color='green', linestyle=':')

ax2.set_xlabel('Round')
ax2.set_ylabel('Gini Coefficient')
ax2.set_title('Gini Coefficient Comparison')
ax2.legend()
ax2.grid(True, alpha=0.3)

plt.tight_layout()
plt.savefig(os.path.join(plots_dir, 'model_comparison_plots.png'), dpi=300, bbox_inches='tight')
plt.show()

# Create detailed comparison plots (similar to wealth_analysis.py comparison style)
fig, axes = plt.subplots(2, 2, figsize=(15, 12))

# Poor count comparison
axes[0,0].plot(baseline_model['Round'], baseline_model['Poor_Count'], 
               label='Baseline', linewidth=2, color='red')
axes[0,0].plot(tax_model['Round'], tax_model['Poor_Count'], 
               label='Tax', linewidth=2, color='blue', linestyle='--', alpha=0.7)
axes[0,0].plot(spreading_model['Round'], spreading_model['Poor_Count'], 
               label='Spreading', linewidth=2, color='green', linestyle=':', alpha=0.7)
axes[0,0].set_title('Poor Population Comparison')
axes[0,0].set_xlabel('Round')
axes[0,0].set_ylabel('Poor Count')
axes[0,0].legend()
axes[0,0].grid(True, alpha=0.3)

# Middle class comparison
axes[0,1].plot(baseline_model['Round'], baseline_model['Middle_Count'], 
               label='Baseline', linewidth=2, color='red')
axes[0,1].plot(tax_model['Round'], tax_model['Middle_Count'], 
               label='Tax', linewidth=2, color='blue', linestyle='--', alpha=0.7)
axes[0,1].plot(spreading_model['Round'], spreading_model['Middle_Count'], 
               label='Spreading', linewidth=2, color='green', linestyle=':', alpha=0.7)
axes[0,1].set_title('Middle Class Population Comparison')
axes[0,1].set_xlabel('Round')
axes[0,1].set_ylabel('Middle Class Count')
axes[0,1].legend()
axes[0,1].grid(True, alpha=0.3)

# Rich count comparison
axes[1,0].plot(baseline_model['Round'], baseline_model['Rich_Count'], 
               label='Baseline', linewidth=2, color='red')
axes[1,0].plot(tax_model['Round'], tax_model['Rich_Count'], 
               label='Tax', linewidth=2, color='blue', linestyle='--', alpha=0.7)
axes[1,0].plot(spreading_model['Round'], spreading_model['Rich_Count'], 
               label='Spreading', linewidth=2, color='green', linestyle=':', alpha=0.7)
axes[1,0].set_title('Rich Population Comparison')
axes[1,0].set_xlabel('Round')
axes[1,0].set_ylabel('Rich Count')
axes[1,0].legend()
axes[1,0].grid(True, alpha=0.3)

# Average wealth comparison
axes[1,1].plot(baseline_model['Round'], baseline_model['Avg_Wealth'], 
               label='Baseline', linewidth=2, color='red')
axes[1,1].plot(tax_model['Round'], tax_model['Avg_Wealth'], 
               label='Tax', linewidth=2, color='blue', linestyle='--', alpha=0.7)
axes[1,1].plot(spreading_model['Round'], spreading_model['Avg_Wealth'], 
               label='Spreading', linewidth=2, color='green', linestyle=':', alpha=0.7)
axes[1,1].set_title('Average Wealth Comparison')
axes[1,1].set_xlabel('Round')
axes[1,1].set_ylabel('Average Wealth')
axes[1,1].legend()
axes[1,1].grid(True, alpha=0.3)

plt.tight_layout()
plt.savefig(os.path.join(plots_dir, 'model_detailed_comparison.png'), dpi=300, bbox_inches='tight')
plt.show()

# Calculate correlation and statistical analysis
print("\n=== Correlation Analysis ===")

# Calculate correlation between models
print("Correlation between Baseline and Tax models:")
metrics = ['Poor_Count', 'Middle_Count', 'Rich_Count', 'Gini_Coefficient', 'Avg_Wealth']

for metric in metrics:
    corr, p_value = pearsonr(baseline_model[metric], tax_model[metric])
    print(f"  {metric}: {corr:.4f} (p-value: {p_value:.4f})")

print("\nCorrelation between Baseline and Spreading models:")
for metric in metrics:
    corr, p_value = pearsonr(baseline_model[metric], spreading_model[metric])
    print(f"  {metric}: {corr:.4f} (p-value: {p_value:.4f})")

print("\nCorrelation between Tax and Spreading models:")
for metric in metrics:
    corr, p_value = pearsonr(tax_model[metric], spreading_model[metric])
    print(f"  {metric}: {corr:.4f} (p-value: {p_value:.4f})")

# Calculate key metrics for each model
print("\n=== Key Performance Metrics ===")

models = [
    ('Baseline', baseline_model),
    ('Tax', tax_model),
    ('Spreading', spreading_model)
]

for model_name, model_df in models:
    initial_poverty = model_df.iloc[0]['Poor_Count'] / model_df.iloc[0]['Population']
    final_poverty = model_df.iloc[-1]['Poor_Count'] / model_df.iloc[-1]['Population']
    poverty_change = (final_poverty - initial_poverty) * 100
    
    initial_gini = model_df.iloc[0]['Gini_Coefficient']
    final_gini = model_df.iloc[-1]['Gini_Coefficient']
    gini_change = final_gini - initial_gini
    
    print(f"\n{model_name} Model:")
    print(f"  Poverty rate change: {poverty_change:+.2f}%")
    print(f"  Gini coefficient change: {gini_change:+.4f}")
    print(f"  Final average wealth: {model_df.iloc[-1]['Avg_Wealth']:.2f}")
    print(f"  Wealth volatility: {model_df['Avg_Wealth'].std():.2f}")

# Calculate model effectiveness
print("\n=== Model Effectiveness Analysis ===")

baseline_final_gini = baseline_model.iloc[-1]['Gini_Coefficient']
tax_final_gini = tax_model.iloc[-1]['Gini_Coefficient']
spreading_final_gini = spreading_model.iloc[-1]['Gini_Coefficient']

print(f"Final Gini Coefficient:")
print(f"  Baseline: {baseline_final_gini:.4f}")
print(f"  Tax: {tax_final_gini:.4f}")
print(f"  Spreading: {spreading_final_gini:.4f}")

print(f"\nGini Reduction vs Baseline:")
print(f"  Tax model: {baseline_final_gini - tax_final_gini:.4f}")
print(f"  Spreading model: {baseline_final_gini - spreading_final_gini:.4f}")

baseline_final_poverty = baseline_model.iloc[-1]['Poor_Count'] / baseline_model.iloc[-1]['Population']
tax_final_poverty = tax_model.iloc[-1]['Poor_Count'] / tax_model.iloc[-1]['Population']
spreading_final_poverty = spreading_model.iloc[-1]['Poor_Count'] / spreading_model.iloc[-1]['Population']

print(f"\nPoverty Rate Reduction vs Baseline:")
print(f"  Tax model: {baseline_final_poverty - tax_final_poverty:.4f}")
print(f"  Spreading model: {baseline_final_poverty - spreading_final_poverty:.4f}")

print(f"\nAnalysis complete! Generated chart files:")
print(f"- {os.path.join(plots_dir, 'model_comparison_plots.png')}: Main three-model comparison trends")
print(f"- {os.path.join(plots_dir, 'model_detailed_comparison.png')}: Detailed three-model comparison")
print(f"\nAll files saved to: {os.path.abspath(plots_dir)}") 