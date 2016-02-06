/**
 * Copyright (C) 2016 Christoph Hennemann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.androidbytes.adbconnect.presentation.observer;


import de.androidbytes.adbconnect.domain.interactor.UseCase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import rx.Observer;
import trikita.log.Log;



@Accessors(prefix = "m")
@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.PRIVATE)
public abstract class UseCaseObserver<T, V extends ObserverHandler> implements Observer<T> {

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
