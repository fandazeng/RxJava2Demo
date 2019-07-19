package zeng.fanda.com.rxjava2demo;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.schedulers.Schedulers;
import zeng.fanda.com.rxjava2demo.bean.Course;
import zeng.fanda.com.rxjava2demo.bean.Student;

/**
 * 变换操作演示
 *
 * @author 曾凡达
 * @date 2019/7/9
 */
public class TransforObservableActivity extends BaseActivity {

    private List<Student> mStudentList;
    private List<Course> mCourseList;

    @Override
    protected int initLayoutId() {
        return R.layout.activity_content;
    }

    @Override
    protected void initData() {
        createStudents();
//        testBuffer();
//        testFlatMap();
//        testConcatMap();
//        testSwitchMap();
//        testMap();

//        testGroupBy();
//        testScan();

        testWindow();
    }

    /**
     * 与 buffer 类似，只是它在发射之前把收集到的数据放进单独的Observable， 而不是放进一个数据结构
     */
    private void testWindow() {
        Disposable window = Observable.range(0, 11).window(2, 1).subscribe(new Consumer<Observable<Integer>>() {

            int count = 1;

            @Override
            public void accept(Observable<Integer> integerObservable) throws Exception {
                Log.d(TAG, " accept线程为：" + count + Thread.currentThread().getName());
                count++;
                Disposable disposable = integerObservable.subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG, "收到消息==" + integer + " == 消息线程为：" + Thread.currentThread().getName());
                    }
                });
            }
        });

    }

    /**
     * Scan	操作符对原始Observable发射的第一项数据应用一个函数，然后将那个函数的结果作为 自己的第一项数据发射。
     * 它将函数的结果同第二项数据一起填充给这个函数来产生它自己的 第二项数，初始值会替换第一个数据
     * 1 - 3 - 6 - 10 - 15
     */
    private void testScan() {
        Observable.range(1, 5).scan(10, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer integer, Integer integer2) throws Exception {
                Log.d(TAG, "apply: integer:" + integer + "  integer2 " + integer2);
                return integer + integer2;
            }
        }).subscribe(mObserver);
    }

    /**
     * 将数据源按照约定进行分组
     */
    private void testGroupBy() {
        Disposable groupBy = Observable.range(1, 20).groupBy(new Function<Integer, Integer>() {

            @Override
            public Integer apply(Integer integer) throws Exception {
                // 根据源始数据进行分组
                return integer % 2;
            }
        }, new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer integer) throws Exception {
                // 对分完组之后的数据的值进行变换处理
                return integer * 3;
            }
        }).subscribe(new Consumer<GroupedObservable<Integer, Integer>>() {
            @Override
            public void accept(GroupedObservable<Integer, Integer> integerIntegerGroupedObservable) throws Exception {
                Log.d(TAG, "GroupBy Key = " + integerIntegerGroupedObservable.getKey());
//                Disposable disposable = integerIntegerGroupedObservable.toList().subscribe(new Consumer<List<Integer>>() {
//                    @Override
//                    public void accept(List<Integer> integers) throws Exception {
//                        Log.d(TAG, "收到消息==" + integers + " == 消息线程为：" + Thread.currentThread().getName());
//                    }
//                });

                // 只打印奇数值
                if (integerIntegerGroupedObservable.getKey() == 1) {
                    Disposable disposable = integerIntegerGroupedObservable.subscribe(new Consumer<Integer>() {
                        @Override
                        public void accept(Integer integer) throws Exception {
                            Log.d(TAG, "收到消息1==" + integer + " == 消息线程为：" + Thread.currentThread().getName());
                        }
                    });
                }

            }
        });
    }

    /**
     * 当原始Observable发射一个新的数据（Observable）时，它将取消订阅并停止监视之前那个数据的Observable，只监视当前这一个.
     * <p>
     * 当数据源较多时，并不一定是只输出最后一项数据，有可能输出几项数据，也可能是全部
     */
    private void testSwitchMap() {
        Observable.range(0, 100000).switchMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) {
                Log.d(TAG, "call: SwitchMap" + Thread.currentThread().getName());
                //如果不通过subscribeOn(Schedulers.newThread())在在子线程模拟并发操作，所有数据源依然会全部输出,也就是并发操作此操作符才有作用
                //若在此通过Thread。sleep（）设置等待时间，则输出信息会不一样。相当于模拟并发程度
                return Observable.just((integer + 100) + "SwitchMap").subscribeOn(Schedulers.newThread());
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(mObserver);
    }

    private void createStudents() {
        mStudentList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Student student = new Student("a", i);
            mCourseList = new ArrayList<>();
            for (int j = 0; j < 30; j++) {
                Course course = new Course();
                course.name = "course:" + i;
                mCourseList.add(course);
            }

            student.mCourses = mCourseList;
            mStudentList.add(student);
        }
    }

    /**
     * 对Observable发射的每一项数据应用一个函数，执行变换操作
     */
    private void testMap() {

        Observable.fromArray(mStudentList.toArray(new Student[mStudentList.size()])).map(new Function<Student, Integer>() {
            @Override
            public Integer apply(Student student) throws Exception {
                return student.age;
            }
        }).subscribe(mObserver);

//        Observable.range(0, 10).map(new Function<Integer, Boolean>() {
//
//            @Override
//            public Boolean apply(Integer integer) throws Exception {
//                Log.d(TAG, "apply：" + integer);
//                return integer > 5;
//            }
//        }).subscribe(mObserver);
    }

    /**
     * FlatMap	将一个发射数据的Observable变换为多个Observables，然后将它们发射的数据合并 后放进一个单独的Observable
     * <p>
     * 保证数据源的顺序性
     */
    private void testConcatMap() {

        Disposable disposable = Observable.fromArray(mStudentList.toArray(new Student[mStudentList.size()])).concatMap(new Function<Student, ObservableSource<Course>>() {
            @Override
            public ObservableSource<Course> apply(Student student) throws Exception {
                return Observable.fromArray(student.mCourses.toArray(new Course[student.mCourses.size()]));
            }
        }).subscribe(new Consumer<Course>() {
            @Override
            public void accept(Course course) throws Exception {
                Log.d(TAG, "concatMap:" + course.name);
            }
        });
    }

    /**
     * FlatMap	将一个发射数据的Observable变换为多个Observables，然后将它们发射的数据合并 后放进一个单独的Observable
     * <p>
     * 不保证数据源的顺序性
     */
    private void testFlatMap() {

        Disposable disposable = Observable.fromArray(mStudentList.toArray(new Student[mStudentList.size()])).flatMap(new Function<Student, ObservableSource<Course>>() {
            @Override
            public ObservableSource<Course> apply(Student student) throws Exception {
                return Observable.fromArray(student.mCourses.toArray(new Course[student.mCourses.size()]));
            }
        }).subscribe(new Consumer<Course>() {
            @Override
            public void accept(Course course) throws Exception {
                Log.d(TAG, "flatMap:" + course.name);
            }
        });
    }

    /**
     * 定期收集Observable的数据放进一个数据包裹，然后发射这些数据包裹，而不是一次发射一 个值
     */
    private void testBuffer() {
        Disposable disposable = Observable.range(0, 11).buffer(2, 5).subscribe(new Consumer<List<Integer>>() {
            @Override
            public void accept(List<Integer> integers) throws Exception {
                Log.d(TAG, "buffer:" + integers);
            }
        });

        // 定期以	List	的形式发射新的数据，每个时间段，收集来自原始 Observable的数据
        Disposable timeSpan = Observable.range(0, 11000).buffer(500, TimeUnit.MILLISECONDS, 10).subscribe(new Consumer<List<Integer>>() {
            @Override
            public void accept(List<Integer> integers) throws Exception {
                Log.d(TAG, "buffer:" + integers);
            }
        });
    }


}
