import time
import base_task, dop1_task, dop2_task, dop3_task

print('> stupid parser')
start_time = time.time()
for i in range(100):
    base_task.base_task()
print("--- %s seconds ---" % (time.time() - start_time))

print('> lib parser')
start_time = time.time()
for i in range(100):
    dop1_task.first()
print("--- %s seconds ---" % (time.time() - start_time))

print('> regexp parser')
start_time = time.time()
for i in range(100):
    dop2_task.second()
print("--- %s seconds ---" % (time.time() - start_time))

print('> formal parser')
start_time = time.time()
for i in range(100):
    dop3_task.third()
print("--- %s seconds ---" % (time.time() - start_time))