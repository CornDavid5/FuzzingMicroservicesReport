from collections import defaultdict
from pyvis.network import Network
import networkx as nx

svc = set()
interactions = defaultdict(lambda: defaultdict(list))

with open('interactions.txt', mode ='r') as file:
    lines = file.readlines()
    for line in lines :
        parts = list(map(str.strip, line.split('|')))
        parts = parts[1:-1]
        svc.add(parts[0])
        svc.add(parts[1])

        additional_info = parts[2:]
        if len(additional_info) == 3:
            # database interactions
            label = 'Database: ' + additional_info[0] + '\nCollection: ' + additional_info[1] + '\nData Sent: ' + additional_info[2]
            interactions[parts[0]][parts[1]].append(label)
        elif additional_info[0] in ['get', 'set']:
            # cache interactions
            label = 'Method: ' + additional_info[0] + '\nData Sent: ' + additional_info[1]
            interactions[parts[0]][parts[1]].append(label)
        else:
            # RPC call
            label = 'RPC Call: ' + additional_info[0] + '\nData Sent: ' + additional_info[1]
            interactions[parts[0]][parts[1]].append(label)

net = Network(heading='Interaction between services within Hotel Reservation', directed=True)
pos = nx.circular_layout(svc, scale=1000)

# add all service node
for node in svc:
    net.add_node(node, label=node, x=pos[node][0], y=-pos[node][1])

# add all edge
for src, des_dict in interactions.items():
    for des, additional_info in des_dict.items():
        title = '\n------\n'.join(sorted(additional_info))
        net.add_edge(src, des, title=title)

net.toggle_physics(False)
# net.show_buttons(filter_=True)
net.write_html('interactions.html', local=False)
