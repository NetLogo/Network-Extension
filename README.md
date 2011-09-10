## THIS CODE CAN'T CURRENTLY BE BUILT OR RUN (EXCEPT AT NORTHWESTERN)

It requires NetLogo 5.0beta5, which isn't out yet.

## TODO

definitely:
* change `extended-link-neighbors` back to `in-link-radius` and take a sourceSet input
* keep -1 as sentinel value, or use false instead?
* split in-link-radius into in-, in-in-, in-out-
* "links in the prim names" ought to be replaceable with a breed name.
  (even if we don't do that now, I guess we ought to give the prim names
  that all have "link" in them?)

maybe:
* always use named arguments when constructing Syntax objects?
* move ArgumentTypeException to api package?
* rename `type` so backticks aren't needed?
* write tests verifying that directed links aren't followed in the wrong direction

## Description of primitives in src directory

Anywhere a link breed is required, `links` is also accepted.

### network:in-link-radius

syntax:  
![turtle](https://github.com/NetLogo/Network-Extension/raw/master/turtle.gif) `TURTLESET network:in-link-radius RADIUS LINK-BREED`

example: `ask one-of bankers [ show other bankers in-network-radius 5 friendships ]`

Returns the set of turtles within the given distance (number of links followed)
of the calling turtle.
Searches breadth-first from the calling turtle,
following links of the given link breed.

### network:link-distance

syntax:  
![turtle](https://github.com/NetLogo/Network-Extension/raw/master/turtle.gif) `network:link-distance TURTLE LINK-BREED`

example: `ask one-of-bankers [ show network:link-distance the-best-banker friendships ]`

Finds the distance to the destination turtle (number of links followed).
Searches breadth-first from the calling turtle,
following links of the given link breed.

Reports -1 if no path exists.

### network:path-turtles

syntax:  
![turtle](https://github.com/NetLogo/Network-Extension/raw/master/turtle.gif) `network:path-turtles TURTLE LINK-BREED`

example: `ask banker1 [ show network:path-turtles banker3 friendships ]`
->   [(banker 1) (banker 2) (banker 3)]
 
Reports a list of turtles following the shortest path from the calling
turtle to the destination turtle.  The calling turtle and the
destination are included in the list.

Reports an empty list if no path exists.

Searches breadth-first from the calling turtle,
following links of the given link breed.

Follows links at the same depth in random order.  If there are
multiple shortest paths, a different path may be returned on
subsequent calls, depending on the random choices made during search.

### network:path-links

syntax:  
![turtle](https://github.com/NetLogo/Network-Extension/raw/master/turtle.gif) `network:path-links TURTLE LINK-BREED`

example: `ask banker1 [ show network:path-links banker3 friendships ]`
->   [(link 1 2) (link 2 3)]

### network:mean-path-length

Reports the average shortest-path length between all distinct pairs of
nodes in the given set of turtles, following links of the given link
breed.

Reports -1 if the turtles are not fully connected.

### Notes

Although these reporters were intended to support both directed and
undirected networks, I'm not positive that they do in all cases.  More
test cases are needed.

It is an open question how `network:link-distance` should handle
distances between two nodes that are not reachable via the network.

Path lengths are computed based solely on the number of hops.  There
isn't currently any way to specify a "weight" or "distance" variable
for links.

## Transition guide

### Renamed primitives

The primitives in this extension were present in NetLogo 4.1, but with different names.
They were renamed as follows:

* `__network-distance` to `network:link-distance`
* `__in-network-radius` to `network:extended-link-neighbors`
* `__average-path-length` to `network:mean-path-length`
* `__network-shortest-path-turtles` to `network:path-turtles`
* `__network-shortest-path-links` to `network:path-links`

### Omitted primitives

The following primitives, present in NetLogo 4.1 but not NetLogo 5.0, are not included in this extension either:

* `__create-network-preferential`
* `__layout-magspring`
* `__layout-quick`
* `__layout-sphere`

For the source code for these primitives, see [this commit](https://github.com/NetLogo/Network-Extension/commit/eea275e20b5c2a76fc76b8b7642d2a5e7df0a1e4).  But note they are written in the style used by built-in NetLogo primitives. To be brought back to life, they'd need to be changed to use the extensions API instead.

## Credits

The first versions of these primitives were written by Forrest Stonedahl.

## Terms of Use

All contents © 2007–2011 Uri Wilensky

The contents of this package may be freely copied, distributed, altered, or otherwise used by anyone for any legal purpose.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
