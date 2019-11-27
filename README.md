PNEditor (Petri Net editor)
========

## The possibilities of improvement

### The complexity of recognizing an arc

***Problem***

Because in our model, we recognize an arc by its id and places don't have the information of its connected arc
, so there are only two ways to find an arc, either by its id, either by the transition which is
connected to it. 

But unfortunately in the PNEditor, the class *GraphicArc* doesn't have id. That leads to an unpleasant situation
that everytime we need to find the corresponding arc of a GraphicArc in our model, we have to iterate every transition to find the transition
which is connected this arc, then iterate on its inArc list and outArc list to find this certain arc.
And to know whether an arc in the model is the graphic arc or not, we have to compare their connected place,
 and then we can finally get the arc object from its arc lists.

***Solution***

Improve the model in the step of conception, and save the connected arc information in places.
Cause we can easily get the connected place id from the GraphicArc and places in model have same id with
GraphicPlace, we can easily obtain the model place id. If we have the connected arc information
in the model place, we are able to get the corresponding model arc in one line instead of 
two layers of for iteration. 


### The code duplication in PetriNetAdapter

***Problem***

In the adapter class, the function *checkIfDeleted* has a part of logic that compares the model
PetriNet with the *GraphicPetriNet* at the moment of firing a transition. It will check whether
every element in the model has the corresponding graphic element in the *GraphicPetrinet*
.

But there is a function *comparePetri* which compare the two PetriNet too and is more
completed than *checkIfDeleted*, because it also checks whether there's something added
in the GraphicPetrtNet and not in the PetriNet model.

***Solution***

Extract the network comparing part from the function *checkIfDeleted*, and combine it with
the function *comparePetri*.




## What we did well

### Applied the singleton design pattern

Although it's a very simple design pattern, we used it to design our model manager class *PetriNetManager*
 and the the adapter *PetriNetAdapter*. It ensure that all objects access the single adapter or manager which 
 guarantees the net information owned by each object is kept in sync.
 
### The separation of model and control level

We have split the control class *PetriManager* from the entities. Entity classes
is not responsible for the logic and changing the their own state. It's also a kind
of Facade Pattern.


Code license: [GNU GPL v3](http://www.gnu.org/licenses/gpl.html)

Requirements: Java SE 6+

