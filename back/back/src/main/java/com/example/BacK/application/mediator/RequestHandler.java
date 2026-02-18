 
package com.example.BacK.application.mediator;
 

public interface RequestHandler<C, R> {
    R handle(C command)  ;
}

