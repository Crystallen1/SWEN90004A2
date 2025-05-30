# WealthWorld Simulation Project

A Java implementation of the NetLogo wealth distribution model, including multiple model variants and in-depth socio-economic analysis capabilities.

## Project Overview

This is a complete multi-agent socio-economic simulation system that implements the classic wealth distribution model based on NetLogo. The project includes multiple different economic model variants for studying wealth distribution, social equity, and the impact of different economic policies on social wealth distribution.

## Project Structure

```
WealthWorld/
├── src/                        # Source code directory
│   ├── core/                   # Core components
│   │   ├── Direction.java      # Direction enumeration definition
│   │   ├── Patch.java          # Patch class (environment unit)
│   │   ├── Turtle.java         # Turtle base class
│   │   └── World.java          # World class (simulation environment base class)
│   ├── models/                 # Different model implementations
│   │   ├── BaselineMain.java   # Baseline model main program
│   │   ├── InheritanceMain.java # Inheritance model main program
│   │   ├── InheritanceWorld.java # Inheritance model world
│   │   ├── InheritanceTurtle.java # Inheritance model turtle
│   │   ├── ComparisonMain.java # Multi-model comparison main program
│   │   ├── ModelComparator.java # Model comparator
│   │   ├── TaxRedistributionWorld.java # Tax redistribution model
│   │   ├── WealthSpreadingWorld.java # Wealth spreading model
│   │   └── WealthSpreadingTurtle.java # Wealth spreading turtle
│   ├── utils/                  # Utility classes
│   │   ├── CsvExporter.java    # Basic CSV export tool
│   │   ├── ComparisonCsvExporter.java # Comparison model CSV export
│   │   └── GiniCalculator.java # Gini coefficient calculator
├── data/                       # Data files directory
│   ├── output/                 # Output data
│   └── plots/                  # Chart output
├── scripts/                    # Python analysis scripts
│   ├── wealth_analysis.py      # Basic model data analysis
│   ├── inheritance_comparison_analysis.py # Inheritance model analysis
│   └── model_comparison_analysis.py # Multi-model comparison analysis
└── docs/                       # Documentation directory
```

## Core Components Detailed

### Core Classes (core/)

#### Direction.java
**Function**: Direction enumeration class
- Defines four basic directions for turtle movement: NORTH, SOUTH, EAST, WEST
- Each direction contains dx, dy coordinate offsets
- Provides basic support for turtle movement in a 2D grid world

```java
// Direction definition example
NORTH(0, -1), SOUTH(0, 1), EAST(1, 0), WEST(-1, 0)
```

#### Patch.java
**Function**: Patch class, represents each grid cell in the environment
- `grainHere`: Current grain amount on this patch
- `maxGrain`: Maximum grain capacity of the patch
- `spreadWealth`: Wealth spread to this patch (used in wealth spreading model)
- `growGrain()`: Grain growth mechanism
- `addSpreadWealth()` / `harvestSpreadWealth()`: Wealth spreading related methods

#### Turtle.java
**Function**: Turtle base class, represents individuals in the simulation
- **Attributes**:
  - `x, y`: Position coordinates
  - `age`: Age
  - `wealth`: Wealth value
  - `metabolism`: Metabolism rate (consumption per round)
  - `vision`: Vision range
  - `lifeExpectancy`: Life expectancy
- **Core Methods**:
  - `decideDirection()`: Decide movement direction (find direction with most grain)
  - `moveEatAgeDie()`: Execute movement, consumption, aging and death check
  - `setInitialTurtleVars()`: Re-initialize turtle attributes (when respawning after death)

#### World.java
**Function**: World base class, manages the entire simulation environment
- **Environment Management**:
  - 2D grid map (`map[][]`)
  - Turtle list (`turtles`)
  - Environment parameters (map size, population, vision range, etc.)
- **Core Mechanisms**:
  - `setupPatches()`: Initialize patches, including grain distribution and spreading algorithm
  - `setupTurtles()`: Create and initialize turtles
  - `step()`: Execute one simulation time step
  - `harvest()`: Turtles harvest grain
  - `growGrain()`: Grain growth
  - `printStats()`: Statistics and output simulation data

### Model Implementations (models/)

#### BaselineMain.java
**Function**: Baseline model main program
- Creates standard wealth distribution simulation
- Configures simulation parameters (50x50 grid, 250 turtles, 300 rounds)
- Exports results to CSV files

#### InheritanceMain.java
**Function**: Inheritance model main program
- Runs parallel comparison of baseline and inheritance models
- Collects and analyzes wealth distribution differences between the two models
- Generates detailed comparison reports and statistics

#### InheritanceWorld.java
**Function**: Inheritance model world implementation
- Inherits from World class
- Uses InheritanceTurtle instead of standard Turtle
- Implements wealth inheritance mechanism

#### InheritanceTurtle.java
**Function**: Turtle supporting wealth inheritance
- Inherits from Turtle class
- Modified `setInitialTurtleVars()` method to retain some wealth for "inheritance"
- Ensures turtles can maintain a certain wealth foundation when respawning

#### ComparisonMain.java
**Function**: Multi-model comparison main program
- Runs three different economic models simultaneously
- Configures all models to use the same parameters and random seeds
- Provides fair model performance comparison

#### ModelComparator.java
**Function**: Model comparator
- Manages parallel execution of multiple model instances
- Collects and compares statistical data from different models
- Generates detailed model performance reports
- Calculates relative differences and trend analysis

#### TaxRedistributionWorld.java
**Function**: Tax redistribution model
- Inherits from World class, uses InheritanceTurtle
- Periodically (every 8 rounds) executes tax redistribution
- Collects 20% tax from the rich (wealth > 2/3 max value)
- Distributes tax revenue equally among the poor (wealth ≤ 1/3 max value)

#### WealthSpreadingWorld.java
**Function**: Wealth spreading model
- Inherits from World class
- Uses WealthSpreadingTurtle turtles
- Modified harvest mechanism to support collecting spread wealth on patches
- Implements mechanism for rich turtles to leave wealth behind when moving

#### WealthSpreadingTurtle.java
**Function**: Wealth spreading turtle
- Inherits from Turtle class
- Rich turtles (wealth > 2/3 max value) leave 20% wealth on original patch when moving
- Contains wealth spreading counter to control spreading frequency

### Utility Classes (utils/)

#### GiniCalculator.java
**Function**: Gini coefficient calculator
- Implements standard Gini coefficient calculation algorithm
- Uses Lorenz curve method
- Returns value between 0-1, where 0 represents perfect equality and 1 represents complete inequality

#### CsvExporter.java
**Function**: Basic CSV data export tool
- Exports wealth distribution data for each simulation round
- Includes statistics for poor, middle class, and rich population counts
- Calculates and exports Gini coefficient
- Supports real-time data writing

#### ComparisonCsvExporter.java
**Function**: CSV export tool for comparison models
- Designed specifically for multi-model comparison
- Supports identification of different model types
- Includes more detailed statistical information (average wealth, population, etc.)
- Facilitates subsequent comparative analysis

### Analysis Scripts (scripts/)

#### wealth_analysis.py
**Function**: Basic model data analysis
- Reads CSV output from baseline model
- Generates wealth distribution trend charts
- Calculates correlation with reference data
- Generates comparison charts and statistical reports

#### inheritance_comparison_analysis.py
**Function**: Inheritance model specific analysis
- Compares differences between baseline and inheritance models
- Analyzes impact of inheritance mechanism on wealth distribution
- Generates detailed comparison charts

#### model_comparison_analysis.py
**Function**: Multi-model comparison analysis
- Analyzes performance differences between three models (inheritance, tax, spreading)
- Generates comprehensive comparison reports
- Visualizes effects of different policies

## Model Descriptions

### 1. Baseline Model
- **Features**: Simulates and replicates the model from NetLogo

### 2. Inheritance Model
- **Features**: Adds wealth inheritance to the baseline model
- **Mechanism**: Turtles retain some wealth when respawning after death

### 3. Tax Redistribution Model
- **Features**: Periodically collects taxes from the rich and distributes to the poor
- **Parameters**: Collects 20% tax from the rich every max_lifespan/8 rounds

### 4. Wealth Spreading Model
- **Features**: Rich turtles leave some wealth behind when moving
- **Parameters**: Rich turtles leave 20% wealth behind every max_lifespan/8 rounds when moving

## Usage Instructions

### Environment Requirements
- Java 21
- Python 3.6+
- Required Python packages: pandas, matplotlib, numpy, scipy, seaborn

### Compile Project
```bash
# In project root directory
javac -cp . src/core/*.java src/utils/*.java src/models/*.java
```

### Run Different Models

#### Baseline Model
```bash
java -cp .:src models.BaselineMain
```

#### Inheritance Model Comparison
```bash
java -cp .:src models.InheritanceMain
```

#### Tax Model and Spread Model Comparison
```bash
java -cp .:src models.ComparisonMain
```

### Data Analysis
```bash
cd scripts
python wealth_analysis.py                    # Baseline model analysis
python inheritance_comparison_analysis.py    # Inheritance model analysis  
python model_comparison_analysis.py          # Multi-model comparison analysis
```

## Output Files

### CSV Data Files
- `wealth_simulation_results.csv`: Baseline model results
- `inheritance_comparison_results.csv`: Inheritance model comparison results
- `model_comparison_results.csv`: Multi-model comparison results

### Visualization Charts
- Various analysis charts (generated by Python scripts)

## Parameter Configuration

### Default Simulation Parameters
- World size: 50×50 grid
- Number of turtles: 250
- Max vision: 5
- Max metabolism: 15  
- Lifespan range: 1-83
- Best land percentage: 10%
- Grain growth interval: 1 round
- Grain growth amount per cycle: 4

### Key Algorithm Parameters
- Tax rate: 20%
- Wealth spreading rate: 20%
- Tax interval: max_lifespan/8 rounds
- Wealth spreading interval: max_lifespan/8 rounds
