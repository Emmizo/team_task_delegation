## Team Task Delegation – Algorithmic Game Theory MVP

This mini-project delivers a minimal yet rigorous decision aid for agile task delegation.  
The engine balances on-going workload, skill alignment, historical performance, duration exposure, and deliberate stretch opportunities so that new initiatives are staffed without overwhelming people while still unlocking growth.

### Mathematical framing

We model the system as:

* `M` – set of members, `P` – set of ongoing projects, `T` – new project/task.
* Each member `m` has expertise vector `E_m(s)∈[0,1]`, performance `Perf_m`, stretch appetite `Grow_m`, and current load `Load_m = Σ_{p∈P_m} dur_p`.
* Task `T` has demand vector `D_T(s)`, duration `Dur_T`, objectives `Obj_T`.

Utility for assigning `m` to `T`:

```
SkillFit_m = Σ_s min(D_T(s), E_m(s)) / Σ_s D_T(s)
Capacity_m = max(0, 1 - Load_m / Cap_nominal)
Growth_m   = Grow_m * (1 - SkillFit_m) * Capacity_m
Objective_m = |Obj_T ∩ ExpertiseKeywords_m| / |Obj_T|
Penalty_m = Dur_T / (2 * Cap_nominal)

U_m = w_c * Capacity_m + w_s * SkillFit_m + w_r * Perf_m
      + w_g * Growth_m + w_o * Objective_m - w_p * Penalty_m
```

Balanced weights default to `(0.25, 0.25, 0.20, 0.15, 0.10, 0.05)` and ensure no single factor dominates.  
Top-`k` candidates (where `k` relates to skill coverage) form the recommended team. If nobody in the initial team offers stretch potential (`Growth_m ≥ 0.25`), the highest-growth member replaces the lowest-ranked person to keep space for skill development.

### Simulation flow

1. Capture or import the roster (skills, workloads, personal drivers).
2. Collect new project demand: duration, skill intensity, objectives.
3. Run the engine to score every member (`AssignmentEngine`) and rank insights.
4. Review the balanced coalition suggestion plus factor-by-factor commentary.

### Running the browser-based MVP

```
mvn spring-boot:run
# or
mvn clean package
java -jar target/team-task-delegation-0.2.0.jar
```

Then open `http://localhost:8081`:

1. Use **Team Members** to capture your roster (the pool starts empty; add at least one member before assigning). Skills use `skill:level`, workload uses `project:weeks`.
2. Go to **Assignment** to describe the new initiative and generate a recommendation.
3. The result page explains the suggested coalition plus per-member reasoning so you can defend the decision in review sessions.

### Command-line simulation (optional)

The console runner mirrors the browser engine but starts empty—add members via prompts:

```
mvn clean package
java -cp target/team-task-delegation-0.2.0.jar com.teamdelegation.simulation.SimulationApp
```

### Extending the work

### Extending the work

- Adjust weights or introduce Shapley-value style contribution metrics to reflect local policies.
- Replace console inputs with structured data (CSV, REST API) for enterprise contexts.
- Incorporate incentive modeling (credits, fatigue decay) for deeper game-theoretic setups.
- Connect to real delivery metrics (cycle time, escaped defects) to auto-tune performance priors.

