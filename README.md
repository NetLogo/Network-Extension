## THIS CODE CAN'T CURRENTLY BE BUILT OR RUN (EXCEPT AT NORTHWESTERN)

It requires NetLogo 5.0beta5, which isn't out yet.

## TODO

* move primitive descriptions out of code and into README.md
* always use named arguments when constructing Syntax objects?
* move ArgumentTypeException to api package?
* rename `type` so backticks aren't needed?
* "links in the prim names" ought to be replaceable with a breed name.
  (even if we don't do that now, I guess we ought to give the prim names
  that all have "link" in them?)
* keep -1 as sentinel value, or use false instead?
* split in-link-radius into in-, in-in-, in-out-
* I got rid of the sourceSet arg to in-link-radius, is that OK?
  hmm, no, Forrest disagrees, and he convinced me.
* should the extended neighborhoods include the turtle itself?
* return paths in forward order or reverse order?
* write tests verifying that directed links aren't followed in the wrong direction

## Description of primitives in src directory

### network:in-link-radius

syntax: `TURTLESET network:in-link-radius RADIUS LINKSET`

example: `ask one-of bankers [ show other bankers in-network-radius 5 friendships ]`

### network:link-distance

syntax: `network:link-distance TURTLE LINK-SET`

example: `ask one-of-bankers [ show network:link-distance the-best-banker friendships ]`

### network:path-turtles

syntax: `network:path-turtles TURTLE LINK-SET`

example: `ask banker1 [ show network:path-turtles banker3 friendships ]`
->   [(banker 1) (banker 2) (banker 3)]
 
### network:path-links

syntax: `network:path-links TURTLE LINK-SET`

example: `ask banker1 [ show network:path-links banker3 friendships ]`
->   [(link 1 2) (link 2 3)]

### __average-path-length

### Notes

Note that it is somewhat of an open question how 
`network:link-distance` should handle distances between two nodes
that are not reachable via the network.  Also, although these
reporters were intended to support both directed and undirected
networks, I'm not positive that they do in all cases.  Also, path
lengths are computed based solely on the number of hops, and there
currently isn't any way to specify a "weight/distance" variable for
the links.

## Description of primitives in src-more directory

These primitives are written in the style used by built-in NetLogo primitives. To be brought back to life, they'd need to be changed to use the extensions API instead.

###__create-network-preferential

syntax:  __create-network-preferential TURTLESET LINK-BREED AVG-DEGREE

example:  __create-network-preferential bankers friendships 3
 (note that you should create the banker turtles ahead of time - this
primitive just creates a BA preferential attachment model network
between the turtles you specify.

### __layout-magspring

### __layout-quick

### __layout-sphere

## Credits

The first versions of these primitives were written by Forrest Stonedahl.

## Terms of Use

All contents © 2007–2011 Uri Wilensky

The contents of this package may be freely copied, distributed, altered, or otherwise used by anyone for any legal purpose.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
