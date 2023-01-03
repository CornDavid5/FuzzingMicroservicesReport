import csv
from collections import defaultdict
from pyvis.network import Network
import networkx as nx

svc = set()
interactions = defaultdict(lambda: defaultdict(list))

with open('interactions.csv', mode ='r') as file:
    reader = csv.reader(file)
    for parts in reader :
        svc.add(parts[0])

        if parts[1] == '':
            # standalone service does not have interaction with other service
            continue

        additional_info = parts[2:]
        if additional_info[-1] == '':
            additional_info[-1] = 'N/A'
        interactions[parts[0]][parts[1]].append(additional_info)

net = Network(heading='Interaction between services within Train Ticket system', directed=True)
pos = nx.circular_layout(svc, scale=1000)

# add all service node
for node in svc:
    net.add_node(node, label=node, x=pos[node][0], y=-pos[node][1])

# add all edge
for src, des_dict in interactions.items():
    if src in ['mysql', 'nacos', 'ts-ui-dashboard', 'ts-news-service', 'ts-avatar-service']:
        continue

    for des, additional_info in des_dict.items():
        if src == 'rabbitmq' or des == 'rabbitmq':
            net.add_edge(src, des, title='topic: ' + additional_info[0][0])
            continue
        elif des == 'mysql':
            net.add_edge(src, des, title='database: ' + src.replace('service', 'mysql'))
            continue

        title = 'URL, Method, Data Passed\n' + '\n'.join(map(lambda x: ', '.join(x), sorted(additional_info, key=lambda r: r[0])))
        net.add_edge(src, des, title=title)

net.toggle_physics(False)
# net.show_buttons(filter_=True)
net.write_html('interactions.html', local=False)
