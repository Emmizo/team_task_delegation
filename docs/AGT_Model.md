# Algorithmic Game Theory Model – Team Task Delegation

## 1. Context

- Agile teams juggle `existing projects (P)` and `incoming work (T)`.
- Each member `m ∈ M` contributes skills, availability, reliability, and appetite for growth.
- Goal: choose a balanced subset `S ⊆ M` for `T` that maximizes delivery confidence while keeping workloads sustainable and providing at least one safe stretch opportunity.

## 2. Variables & Parameters

| Symbol | Description |
|--------|-------------|
| `E_m(s)` | skill proficiency of member `m` on skill `s` (0-1) |
| `Load_m` | sum of remaining weeks for member `m` on active projects |
| `Perf_m` | normalized recent performance |
| `Grow_m` | willingness to stretch into new domains |
| `Cap_nominal` | nominal sustainable capacity window (weeks) |
| `D_T(s)` | skill intensity demanded by project `T` |
| `Dur_T` | duration of project `T` |
| `Obj_T` | set of objectives / success criteria |

## 3. Scoring Functions

1. **Capacity**  
   `Capacity_m = max(0, 1 - Load_m / Cap_nominal)`

2. **Skill fit**  
   `SkillFit_m = Σ_s min(D_T(s), E_m(s)) / Σ_s D_T(s)`

3. **Growth (stretch opportunity)**  
   `Growth_m = Grow_m · (1 - SkillFit_m) · Capacity_m`

4. **Objective alignment**  
   `Objective_m = |Obj_T ∩ Keywords(E_m)| / max(1, |Obj_T|)`

5. **Duration penalty**  
   `Penalty = Dur_T / (2 · Cap_nominal)`

6. **Utility**  
   `U_m = w_c Capacity_m + w_s SkillFit_m + w_r Perf_m + w_g Growth_m + w_o Objective_m - w_p Penalty`

Weights \(w\) default to `(0.25, 0.25, 0.20, 0.15, 0.10, 0.05)` but can be tuned.

## 4. Selection Heuristic

1. Score every `m` with `U_m`.
2. Sort descending by `U_m`.
3. Let `k = max(2, ceil(|RequiredSkills|))`. Select first `k` members.
4. If selected team lacks a member with `Growth_m ≥ 0.25`, swap the lowest `U_m` person for the highest-growth candidate to guarantee skill development.

This heuristic approximates a cooperative game perspective where individual contributions (capacity, skills) and coalition benefits (coverage, growth diversity) are balanced without solving an NP-hard optimization.

## 5. Implementation Layers (Java/Spring Boot)

- `AssignmentEngine` – pure algorithm module.
- `ScenarioRepository` – lightweight in-memory state shared between HTTP flows (starts empty; populate via UI or API).
- `MemberController` / `AssignmentController` – Thymeleaf MVC endpoints for data entry and decision review.
- `SimulationApp` (CLI) – available for scripting or quick demos; it also starts with an empty roster so you can add only the members you need for that experiment.

## 6. Usage

Run the browser workflow:

```
mvn spring-boot:run
# http://localhost:8081
```

Prefer CLI experimentation?

```
mvn clean package
java -cp target/team-task-delegation-0.2.0.jar com.teamdelegation.simulation.SimulationApp
```

Either interface produces the same recommendation logic, keeping academic write-ups aligned with the implementation.

To run app = "mvn spring-boot:run"