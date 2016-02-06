package de.androidbytes.adbconnect.presentation.observer;


import de.androidbytes.adbconnect.domain.interactor.UseCase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import rx.Observer;
import trikita.log.Log;


/**
 * Created by Christoph on 26.09.2015.
 */
@Accessors(prefix = "m")
@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.PRIVATE)
public abstract class UseCaseObserver<T,V extends ObserverHandler> implements Observer<T> {

    private UseCase mUseCase;
    private ObserverHandler mResultHandler;
    private boolean mMultipleExecutionsPossible;
    private boolean mMultipleSimultaneousExecutionsPossible;
    private boolean mAlreadyExecuted;

    @Getter(AccessLevel.PUBLIC)
    private boolean mCurrentlyRunning;

    public UseCaseObserver(ObserverHandler observerHandler, UseCase useCase, boolean multipleExecutionsPossible, boolean multipleSimultaneousExecutionsPossible) {
        setResultHandler(observerHandler);
        setUseCase(useCase);
        setMultipleExecutionsPossible(multipleExecutionsPossible);
        setMultipleSimultaneousExecutionsPossible(multipleSimultaneousExecutionsPossible);
    }

    public boolean execute() {

        if(!isAlreadyExecuted() || isMultipleExecutionsPossible()) {

            if(!isAlreadyExecuted()) {
                setAlreadyExecuted(true);
            }

            if (isMultipleSimultaneousExecutionsPossible() || !isCurrentlyRunning()) {
                setCurrentlyRunning(true);
                getUseCase().execute(this);
                return true;
            }
        }

        return false;
    }

    public void unsubscribeObserver() {
        getUseCase().unsubscribe();
        setUseCase(null);
        setResultHandler(null);
    }

    @Override
    public void onCompleted() {
        setCurrentlyRunning(false);
        completed();
    }

    protected abstract void completed();

    @Override
    public void onError(Throwable e) {

        Log.e(e);
        e.printStackTrace();
        error(e);
    }

    protected abstract void error(Throwable e);

    @Override
    public void onNext(T t) {
        next(t);
    }

    protected abstract void next(T t);
}
