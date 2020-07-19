//
// Created by asalehin on 7/18/20.
//

#ifndef FAST_MIXER_TASKQUEUE_H
#define FAST_MIXER_TASKQUEUE_H

#ifndef MODULE_NAME
#define MODULE_NAME "ThreadPool"
#endif

#include <queue>
#include <atomic>
#include <thread>
#include <functional>

using namespace std;

class TaskQueue {

public:
    TaskQueue() {
        start_queue();
    }

    void stop_queue() {
        is_running = false;
    }

    template<class F, class... Args>
    auto enqueue(F&& f, Args&&... args) {
        auto task = std::bind(std::forward<F>(f), std::forward<Args>(args)...);
        auto func = [task](){ task(); };
        q.push(func);
    }

private:
    const char* TAG = "TaskQueue:: %d";
    queue<std::function<void()>> q;
    atomic<bool> is_running;
    thread t;

    void start_queue() {
        is_running = true;
        t = thread([this] {
            this->executor_loop();
        });
        t.detach();
    }

    void executor_loop() {
        int i = 0;
        while (is_running) {
            if (!q.empty()) {
                auto f = q.front();
                f();
                q.pop();
            }
        }
    }
};

#endif //FAST_MIXER_TASKQUEUE_H