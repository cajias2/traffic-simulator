package sim.graph.social.metrics.commTracker;

import java.util.ArrayList;
import java.util.List;

import sim.mason.AgentNetwork;

public class TimeLineList {
	private List<List<Community>> _timeLine;

	public TimeLineList() {
		_timeLine = new ArrayList<List<Community>>();
	}

	public void add(int time_, List<Integer> comm_, AgentNetwork graph_) {

		if (_timeLine.isEmpty() || (time_ == _timeLine.size() + 1)) {
			List<Community> aux = new ArrayList<Community>();
			_timeLine.add(aux);
		}

		if ((time_ < 1) || (time_ > _timeLine.size() + 1)) {
			return;
		}
		Community newComm = new Community(comm_, graph_);
		_timeLine.get(time_ - 1).add(newComm);
		searchPredecessors(newComm);
	}

	public List<Community> get(int pos_) {
		return _timeLine.get(pos_);
	}

	private void searchPredecessors(Community comm_) {
		List<Integer> newCores = comm_.getCoreNodes();
		List<Integer> newNodes = comm_.getAllNodes();

		if (_timeLine.size() > 1) {
			int currentTime = _timeLine.size() - 1;
			List<Community> previousSnapshot = _timeLine.get(currentTime - 1);

			List<Community> predecessors = new ArrayList<Community>();
			for (Community read : previousSnapshot) {
				List<Integer> readCores = read.getCoreNodes();
				for (Integer node : readCores) {
					if (newNodes.contains(node)
							&& (!predecessors.contains(read))) {
						predecessors.add(read);
					}
				}
			}

			for (Community pred : predecessors) {
				List<Community> ancestors = pred.getPredecessors();

				if (!ancestors.isEmpty()) {
					boolean added = false;
					int pointer = 0;
					while (!added && pointer < ancestors.size()) {
						Community ancestor = ancestors.get(pointer);
						pointer++;
						List<Integer> nodesAncestor = ancestor.getAllNodes();
						boolean found = false;
						int secondPointer = 0;

						while (!found && (secondPointer < newCores.size())) {
							int newCore = newCores.get(secondPointer);
							secondPointer++;

							found |= nodesAncestor.contains(newCore);
						}

						if (found) {
							comm_.addPredecessor(pred);
							System.out.println(pred + " --> " + comm_);
							pred.addSuccessor(comm_);
							added = true;
						} else {
							List<Community> olderAncestors = ancestor
									.getPredecessors();
							if (!olderAncestors.isEmpty())
								ancestors.addAll(olderAncestors);
						}
					}
				} else {
					comm_.addPredecessor(pred);
					System.out.println(pred + " --> " + comm_);
					pred.addSuccessor(comm_);
				}
			}
		}

	}
}
