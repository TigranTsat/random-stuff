import math
import json
from prettytable import PrettyTable

PRI_MAX = 63
LOAD_AVG = 0
TIME_SLICE = 4
QUEUES = {}
for i in range(0, 64):
    QUEUES[i] = []

class Thread(object):
    def __init__(self, name, nice):
        self.recent_cpu = 0.0
        self.nice = nice
        self.priority = self.calculate_priority()
        self.name = name

    def on_run(self):
        self.recent_cpu += 1

    def calculate_priority(self):
        # priority = PRI_MAX - (recent_cpu / 4) - (nice * 2),
        return math.floor(PRI_MAX - (self.recent_cpu / 4) - (self.nice * 2))

    def recalculate_priority(self):
        self.priority = self.calculate_priority()

def update_load_average():
    pass
    #  TIMER_FREQ 100, hence recalcuation will NOT happen until 100 ticks. Our range is lower.



def queues_init(thrds):
    for t in thrds:
        QUEUES[t.priority].append(t)


def update_priorities(thrds, thread_that_ran):
    QUEUES[thread_that_ran.priority].remove(thread_that_ran)
    QUEUES[thread_that_ran.priority].append(thread_that_ran)
    for t in thrds:
        old_pri = t.priority
        t.recalculate_priority()
        new_pri = t.priority
        if old_pri != new_pri:
            QUEUES[old_pri].remove(t)
            QUEUES[new_pri].append(t)


def get_thread_to_run(thrds):
    max_prio_thread = max(thrds, key=lambda x: x.priority)
    assert isinstance(QUEUES[max_prio_thread.priority], list)
    return QUEUES[max_prio_thread.priority][0]



# Suppose threads A, B, and C have nice values 0, 1, and 2
threads = [Thread(name="A", nice=0), Thread(name="B", nice=1), Thread(name="C", nice=2)]


simulation_results = []
queues_init(threads)

# Simulate
thread_to_run = None
for i in range(0, 40):
    simulation_element = {
        "iteration": i,
        "priorities": {},
        "recent_cpu": {}
    }
    for thread in threads:
        simulation_element["priorities"][thread.name] = thread.priority
        simulation_element["recent_cpu"][thread.name] = thread.recent_cpu

    if i > 0 and (i % 4 == 0):
        update_priorities(threads, thread_to_run)

    if (thread_to_run is None) or (i % 4 == 0):
        thread_to_run = get_thread_to_run(threads)

    # print("For tick %s running %s" % (i, thread_to_run.name))
    simulation_element["thread_to_run"] = thread_to_run.name
    thread_to_run.on_run()




    simulation_results.append(simulation_element)


# print json.dumps(simulation_results)


#
# timer  recent_cpu    priority   thread
# ticks   A   B   C   A   B   C   to run
# -----  --  --  --  --  --  --   ------


# Print data
t = PrettyTable(['Ticks', 'A cpu', 'B cpu', 'C cpu', 'A prio', 'B prio', 'C prio', 'thread to run'])
for sim_result in simulation_results:
    cpu = sim_result["recent_cpu"]
    prio = sim_result["priorities"]
    t.add_row([sim_result["iteration"], cpu["A"], cpu["B"], cpu["C"], prio["A"],prio["B"], prio["C"],sim_result["thread_to_run"]])
print(t)



