package de.androidbytes.adbconnect.domain.interactor;


import de.androidbytes.adbconnect.domain.executor.PostExecutionThread;
import de.androidbytes.adbconnect.domain.executor.ThreadExecutor;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;


/**
 * Created by Christoph on 19.09.2015.
 */
public abstract class UseCase {

    private final ThreadExecutor threadExecutor;
    private final PostExecutionThread postExecutionThread;

    private Subscription subscription = Subscriptions.empty();

    protected UseCase(ThreadExecutor threadExecutor,
                      PostExecutionThread postExecutionThread) {
        this.threadExecutor = threadExecutor;
        this.postExecutionThread = postExecutionThread;
    }

    /**
     * Builds an {@link rx.Observable} which will be used when executing the current {@link UseCase}.
     */
    protected abstract Observable buildUseCaseObservable();

    /**
     * Executes the current use case.
     *
     * @param UseCaseSubscriber The guy who will be listen to the observable build with {@link #buildUseCaseObservable()}.
     */
    @SuppressWarnings("unchecked")
    public void execute(Observer UseCaseSubscriber) {

        this.subscription = this.buildUseCaseObservable()
                    .subscribeOn(Schedulers.from(threadExecutor))
                    .observeOn(postExecutionThread.getScheduler())
                    .subscribe(UseCaseSubscriber);
    }

    /**
     * Unsubscribes from current {@link rx.Subscription}.
     */
    public void unsubscribe() {
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
